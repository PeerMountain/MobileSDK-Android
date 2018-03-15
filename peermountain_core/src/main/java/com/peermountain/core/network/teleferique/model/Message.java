package com.peermountain.core.network.teleferique.model;

/**
 * Created by Galeen on 3/14/2018.
 */

public class Message {
    //    public String serviceID; //"sha256",
//    public String consumerID; //"consumerServiceInstance",
//    public String dossierSalt; // "salt/40byte Entropy",
//    public int bodyType; //: "bodyTypeCode", from TfConstants
//    public String signature; //"XAdES-T on Body",
//    public MessageBody body; //"msgBody Structure"
    public String messageHash; // sha256
    public String messageType; // typecode , from TfConstants
    public String dossierHash; // hmac-sha256
    public String bodyHash; //sha256
    public String messageSig; // RSA signature
    public String message; // (enc)BLOB - AESEncryptedBlob!
    public String sender; // PM Address

//first MessageContent
//    def build(self, passphrase):
//    messageBody = self.messageBody.build()
//    messageBody_raw = messageBody['messageBody'].decode()
//    cipher = AES(passphrase)
//    messageContent = cipher.encrypt(self.pack(messageBody_raw))
//            return {
//        'dossierHash': base64.b64encode(HMAC.new(
//                self.content['dossierSalt'], messageBody['messageBody'], SHA256).digest()).decode(),
//                'message':messageContent.decode(),
//                'messageHash':base64.b64encode(SHA256.new(messageContent).digest()).decode(),
//                'bodyHash':messageBody['bodyHash'],
//                'messageType': self.messageType
//    }

//second MessageBody
//    def build(self, identity, client):
//    build_content = self.message_content.build(self.passphrase='Peer Mountain')
//    content = {
//        'sender': identity.address,
//                'messageSig': identity.sign_message(build_content['messageHash'],client),
//                'messageType': build_content['messageType'],
//                'messageHash': build_content['messageHash'],
//                'dossierHash': build_content['dossierHash'],
//                'bodyHash': build_content['bodyHash'],
//                'message': build_content['message']
//    }
//    print(content)
//        return content

}
