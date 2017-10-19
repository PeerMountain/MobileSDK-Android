package com.peermountain.sdk.ui.authorized.contacts;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.peermountain.core.model.guarded.Contact;
import com.peermountain.sdk.R;
import com.peermountain.sdk.ui.authorized.contacts.ContactsFragment.OnListFragmentInteractionListener;
import com.peermountain.sdk.ui.authorized.settings.ProfileSettingsFragment;

import java.util.List;


public class MyContactsRecyclerViewAdapter extends RecyclerView.Adapter<MyContactsRecyclerViewAdapter.ViewHolder> {

    private final List<Contact> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyContactsRecyclerViewAdapter(List<Contact> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pm_fragment_contact_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bind(mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView tvName;
        public final TextView tvContent;
        public final ImageView ivAvatar;
        public Contact mContact;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tvName = (TextView) view.findViewById(R.id.tvName);
            tvContent = (TextView) view.findViewById(R.id.tvContent);
            ivAvatar = view.findViewById(R.id.ivAvatar);


            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mListener) {
                        // Notify the active callbacks interface (the activity, if the
                        // fragment is attached to one) that an item has been selected.
                        mListener.onContactSelected(mContact);
                    }
                }
            });
        }

        public void bind(Contact contact) {
            mContact = contact;
            tvName.setText(contact.getNames());
            tvContent.setText(contact.getPob());
            ProfileSettingsFragment.loadAvatar(tvContent.getContext(),contact,ivAvatar);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvName.getText() + "'";
        }
    }
}
