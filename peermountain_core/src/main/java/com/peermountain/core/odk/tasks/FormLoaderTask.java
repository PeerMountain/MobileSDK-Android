package com.peermountain.core.odk.tasks;

/*
 * Copyright (C) 2009 University of Washington
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */


import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.Nullable;

import com.peermountain.core.odk.FormController;
import com.peermountain.core.odk.model.FileReferenceFactory;
import com.peermountain.core.odk.utils.Collect;
import com.peermountain.core.odk.utils.FileUtils;
import com.peermountain.core.utils.LogUtils;
import com.peermountain.core.utils.PmCoreUtils;

import org.javarosa.core.model.FormDef;
import org.javarosa.core.model.FormIndex;
import org.javarosa.core.model.instance.InstanceInitializationFactory;
import org.javarosa.core.model.instance.TreeElement;
import org.javarosa.core.model.instance.TreeReference;
import org.javarosa.core.reference.ReferenceManager;
import org.javarosa.core.reference.RootTranslator;
import org.javarosa.core.services.PrototypeManager;
import org.javarosa.core.util.externalizable.DeserializationException;
import org.javarosa.core.util.externalizable.ExtUtil;
import org.javarosa.form.api.FormEntryController;
import org.javarosa.form.api.FormEntryModel;
import org.javarosa.model.xform.XFormsModule;
import org.javarosa.xform.parse.XFormParseException;
import org.javarosa.xform.parse.XFormParser;
import org.javarosa.xform.util.XFormUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Background task for loading a form.
 *
 * @author Carl Hartung (carlhartung@gmail.com)
 * @author Yaw Anokwa (yanokwa@gmail.com)
 */
public class FormLoaderTask extends AsyncTask<String, String, FormLoaderTask.FECWrapper> {
    private final static String t = "FormLoaderTask";
    /**
     * Classes needed to serialize objects. Need to put anything from JR in here.
     */
    public final static String[] SERIALIABLE_CLASSES = {
            "org.javarosa.core.services.locale.ResourceFileDataSource", // JavaRosaCoreModule
            "org.javarosa.core.services.locale.TableLocaleSource", // JavaRosaCoreModule
            "org.javarosa.core.model.FormDef",
            "org.javarosa.core.model.SubmissionProfile", // CoreModelModule
            "org.javarosa.core.model.QuestionDef", // CoreModelModule
            "org.javarosa.core.model.GroupDef", // CoreModelModule
            "org.javarosa.core.model.instance.FormInstance", // CoreModelModule
            "org.javarosa.core.model.data.BooleanData", // CoreModelModule
            "org.javarosa.core.model.data.DateData", // CoreModelModule
            "org.javarosa.core.model.data.DateTimeData", // CoreModelModule
            "org.javarosa.core.model.data.DecimalData", // CoreModelModule
            "org.javarosa.core.model.data.GeoPointData", // CoreModelModule
            "org.javarosa.core.model.data.IntegerData", // CoreModelModule
            "org.javarosa.core.model.data.LongData", // CoreModelModule
            "org.javarosa.core.model.data.MultiPointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.PointerAnswerData", // CoreModelModule
            "org.javarosa.core.model.data.SelectMultiData", // CoreModelModule
            "org.javarosa.core.model.data.SelectOneData", // CoreModelModule
            "org.javarosa.core.model.data.StringData", // CoreModelModule
            "org.javarosa.core.model.data.TimeData", // CoreModelModule
            "org.javarosa.core.model.data.UncastData", // CoreModelModule
            "org.javarosa.core.model.data.helper.BasicDataPointer" // CoreModelModule
    };

    private FormLoaderListener mStateListener;
    private String mErrorMsg;
    private final String mInstancePath;
    private final String mXPath;
    private final String mWaitingXPath;
    private boolean pendingActivityResult = false;
    private int requestCode = 0;
    private int resultCode = 0;
    private Intent intent = null;
    public String formPath;

    protected class FECWrapper {
        FormController controller;
        boolean usedSavepoint;


        protected FECWrapper(FormController controller, boolean usedSavepoint) {
            this.controller = controller;
            this.usedSavepoint = usedSavepoint;
        }


        protected FormController getController() {
            return controller;
        }

        protected boolean hasUsedSavepoint() {
            return usedSavepoint;
        }

        protected void free() {
            controller = null;
        }
    }

    public interface FormLoaderListener {
        void loadingComplete(FormLoaderTask task);

        void loadingError(String errorMsg);
    }

    private FECWrapper data;

    public FormLoaderTask(String instancePath, String XPath, String waitingXPath) {
        mInstancePath = instancePath;
        mXPath = XPath;
        mWaitingXPath = waitingXPath;
    }

    /**
     * Initialize {@link FormEntryController} with {@link FormDef} from binary or from XML. If given
     * an instance, it will be used to fill the {@link FormDef}.
     */
    @Override
    protected FECWrapper doInBackground(String... path) {
        FormEntryController formEntryController = null;
        FormDef formDef = null;
        mErrorMsg = null;

        formPath = path[0];

        String instanceFileName = formPath.substring(formPath.lastIndexOf('/') + 1,
                formPath.lastIndexOf('.'));

        File instanceFile = PmCoreUtils.getAnswersForXForm(instanceFileName);
//        if(isRealFile(instanceFile)){
//            mInstancePath = instanceFile.getAbsolutePath();
//        }

        File formXmlFile = new File(formPath);
        formDef = getFormDefFromFileOrCache(formXmlFile);

        if (mErrorMsg != null || formDef ==null) {
            return null;
        }

        // new evaluation context for function handlers
//        fd.setEvaluationContext(new EvaluationContext(null));

        // create FormEntryController from formdef
        FormEntryModel formEntryModel = new FormEntryModel(formDef);
        formEntryController = new FormEntryController(formEntryModel);

        boolean usedSavepoint = false;
        final InstanceInitializationFactory instanceInit = new InstanceInitializationFactory();
        try {
            // import existing data into formdef
            if (isRealFile(instanceFile)) {
//                File instanceFile = new File(mInstancePath);
                File shadowInstanceFile = getSavepointFile(instanceFile.getName());
                if (shadowInstanceFile.exists() &&
                        (shadowInstanceFile.lastModified() > instanceFile.lastModified())) {
                    // the savepoint is newer than the saved value of the instance.
                    // use it.
                    usedSavepoint = true;
                    instanceFile = shadowInstanceFile;
                    LogUtils.w(t, "Loading instance from shadow file: " + shadowInstanceFile.getAbsolutePath());
                }
                if (instanceFile.exists()) {
                    // This order is important. Import data, then initialize.
                    importData(instanceFile, formEntryController);
                    formDef.initialize(false, instanceInit);
                } else {
                    formDef.initialize(true, instanceInit);
                }
            } else {
                formDef.initialize(true, instanceInit);
            }
        } catch (RuntimeException e) {
            mErrorMsg = e.getMessage();
            return null;
        }

        // set paths to /data/user/0/com.peermountain.dev/files/pm_xforms/forms/formfilename-media/
        String formFileName = formXmlFile.getName().substring(0, formXmlFile.getName().lastIndexOf("."));
        File formMediaDir = new File(formXmlFile.getParent(), formFileName + "-media");

        // Remove previous forms
        ReferenceManager.instance().clearSession();

        // This should get moved to the Application Class
        if (ReferenceManager.instance().getFactories().length == 0) {
            // this is /data/user/0/com.peermountain.dev/files/pm_xforms
            ReferenceManager.instance().addReferenceFactory(
                    new FileReferenceFactory(Collect.ODK_ROOT));
        }

        // Set jr://... to point to /data/user/0/com.peermountain.dev/files/pm_xforms/forms/filename-media/
        ReferenceManager.instance().addSessionRootTranslator(
                new RootTranslator("jr://images/", "jr://file/forms/" + formFileName + "-media/"));
        ReferenceManager.instance().addSessionRootTranslator(
                new RootTranslator("jr://image/", "jr://file/forms/" + formFileName + "-media/"));
        ReferenceManager.instance().addSessionRootTranslator(
                new RootTranslator("jr://audio/", "jr://file/forms/" + formFileName + "-media/"));
        ReferenceManager.instance().addSessionRootTranslator(
                new RootTranslator("jr://video/", "jr://file/forms/" + formFileName + "-media/"));

        // clean up vars
        formDef = null;
        formXmlFile = null;
//        formPath = null;

        FormController fc = new FormController(formMediaDir, formEntryController,
                !isRealFile(instanceFile) ? null : instanceFile);
        if (mXPath != null) {
            // we are resuming after having terminated -- set index to this position...
            FormIndex idx = fc.getIndexFromXPath(mXPath);
            fc.jumpToIndex(idx);
        }
        if (mWaitingXPath != null) {
            FormIndex idx = fc.getIndexFromXPath(mWaitingXPath);
            fc.setIndexWaitingForData(idx);
        }
        data = new FECWrapper(fc, usedSavepoint);
        return data;

    }

    private boolean isRealFile(File instanceFile) {
        return instanceFile!=null && instanceFile.exists();
    }

    @Nullable
    private FormDef getFormDefFromFileOrCache(File formXmlFile) {
        FormDef formDef = null;
        String formHash = FileUtils.getMd5Hash(formXmlFile);
        File formBinFile = new File(Collect.CACHE_PATH + File.separator
                + formHash + ".formdef");

        if (formBinFile.exists()) {
            // if we have binary, deserialize binary
            LogUtils.i(t, "Attempting to load " + formXmlFile.getName()
                    + " from cached file: " + formBinFile.getAbsolutePath());
            formDef = deserializeFormDef(formBinFile);
            if (formDef == null) {
                // some error occured with deserialization. Remove the file, and make a new .formdef
                // from xml
                LogUtils.w(t, "Deserialization FAILED!  Deleting cache file: "
                        + formBinFile.getAbsolutePath());
                formBinFile.delete();
            }
        }
        if (formDef == null) {
            // no binary, read from xml file
            try {
                LogUtils.i(t, "Attempting to load from: " + formXmlFile.getAbsolutePath());
                FileInputStream fileInputStream = new FileInputStream(formXmlFile);
                formDef = XFormUtils.getFormFromInputStream(fileInputStream);
                if (formDef == null) {
                    mErrorMsg = "Error reading XForm file";
                } else {
                    serializeFormDef(formDef, formXmlFile);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                mErrorMsg = e.getMessage();
            } catch (XFormParseException e) {
                mErrorMsg = e.getMessage();
                e.printStackTrace();
            } catch (Exception e) {
                mErrorMsg = e.getMessage();
                e.printStackTrace();
            }
        }
        return formDef;
    }


    private boolean importData(File instanceFile, FormEntryController formEntryController) {
        // convert files into a byte array
        byte[] fileBytes = FileUtils.getFileAsBytes(instanceFile);

        // get the root of the saved and template instances
        TreeElement savedRoot = XFormParser.restoreDataModel(fileBytes, null).getRoot();
        TreeElement templateRoot = formEntryController.getModel().getForm().getInstance().getRoot().deepCopy(true);

        // weak check for matching forms
        if (!savedRoot.getName().equals(templateRoot.getName()) || savedRoot.getMult() != 0) {
            LogUtils.e(t, "Saved form instance does not match template form definition");
            return false;
        } else {
            // populate the data model
            TreeReference treeReference = TreeReference.rootRef();
            treeReference.add(templateRoot.getName(), TreeReference.INDEX_UNBOUND);
            templateRoot.populate(savedRoot, formEntryController.getModel().getForm());

            // populated model to current form
            formEntryController.getModel().getForm().getInstance().setRoot(templateRoot);

            // fix any language issues
            // : http://bitbucket.org/javarosa/main/issue/5/itext-n-appearing-in-restored-instances
            if (formEntryController.getModel().getLanguages() != null) {
                formEntryController.getModel()
                        .getForm()
                        .localeChanged(formEntryController.getModel().getLanguage(),
                                formEntryController.getModel().getForm().getLocalizer());
            }

            return true;

        }
    }


    /**
     * Read serialized {@link FormDef} from file and recreate as object.
     *
     * @param formDef serialized FormDef file
     * @return {@link FormDef} object
     */
    public FormDef deserializeFormDef(File formDef) {

        // TODO: any way to remove reliance on jrsp?

        // need a list of classes that formdef uses
        // unfortunately, the JR registerModule() functions do more than this.
        // register just the classes that would have been registered by:
        // new JavaRosaCoreModule().registerModule();
        // new CoreModelModule().registerModule();
        // replace with direct call to PrototypeManager
        PrototypeManager.registerPrototypes(SERIALIABLE_CLASSES);
        new XFormsModule().registerModule();

        FileInputStream fis = null;
        FormDef fd = null;
        try {
            // create new form def
            fd = new FormDef();
            fis = new FileInputStream(formDef);
            DataInputStream dis = new DataInputStream(fis);

            // read serialized formdef into new formdef
            fd.readExternal(dis, ExtUtil.defaultPrototypes());
            dis.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fd = null;
        } catch (IOException e) {
            e.printStackTrace();
            fd = null;
        } catch (DeserializationException e) {
            e.printStackTrace();
            fd = null;
        } catch (Exception e) {
            e.printStackTrace();
            fd = null;
        }

        return fd;
    }


    /**
     * Write the FormDef to the file system as a binary blog.
     *
     * @param fileXml the form file
     */
    private void serializeFormDef(FormDef fd, File fileXml) {
        // calculate unique md5 identifier
        String hash = FileUtils.getMd5Hash(fileXml);
        File formDef = new File(Collect.CACHE_PATH + File.separator + hash + ".formdef");

        // formdef does not exist, create one.
        if (!formDef.exists()) {
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(formDef);
                DataOutputStream dos = new DataOutputStream(fos);
                fd.writeExternal(dos);
                dos.flush();
                dos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onPostExecute(FECWrapper wrapper) {
        synchronized (this) {
            if (mStateListener != null) {
                if (wrapper == null) {
                    mStateListener.loadingError(mErrorMsg);
                } else {
                    mStateListener.loadingComplete(this);
                }
            }
        }
    }


    public void setFormLoaderListener(FormLoaderListener sl) {
        synchronized (this) {
            mStateListener = sl;
        }
    }

    public FormController getFormController() {
        return (data != null) ? data.getController() : null;
    }

    public boolean hasUsedSavepoint() {
        return (data != null) ? data.hasUsedSavepoint() : false;
    }

    public void destroy() {
        if (data != null) {
            data.free();
            data = null;
        }
    }

    public boolean hasPendingActivityResult() {
        return pendingActivityResult;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public int getResultCode() {
        return resultCode;
    }

    public Intent getIntent() {
        return intent;
    }

    public void setActivityResult(int requestCode, int resultCode, Intent intent) {
        this.pendingActivityResult = true;
        this.requestCode = requestCode;
        this.resultCode = resultCode;
        this.intent = intent;
    }

    /**
     * Return the savepoint file for a given instance.
     */
    static File getSavepointFile(String instanceName) {
        File tempDir = new File(Collect.CACHE_PATH);
        return new File(tempDir, instanceName + ".save");
    }

}
