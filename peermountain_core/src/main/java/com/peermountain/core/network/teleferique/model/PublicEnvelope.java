package com.peermountain.core.network.teleferique.model;

import com.peermountain.core.secure.SecureHelper;

import java.io.IOException;

/**
 * Created by Galeen on 3/14/2018.
 */

public class PublicEnvelope {
    public String messageHash; // sha256
    public String messageType; // typecode , from TfConstants
    public String dossierHash; // hmac-sha256
    public String bodyHash; //sha256
    public String messageSig; // RSA signature
    public String message; // (enc)BLOB - AESEncryptedBlob!
    public String sender; // PM Address

    public PublicEnvelope() {
    }

    public PublicEnvelope(Invitation invitation) {
        try {
            String messageBody = SecureHelper.toString(invitation);
            String pass = "Peer Mountain";
            //AES encrypt body with passphrase='Peer Mountain'
            String encryptedBody = SecureHelper.encodeAES(pass,messageBody);
            message = encryptedBody;
            messageHash = SecureHelper.getHashAsString(encryptedBody);
            bodyHash = SecureHelper.getHashAsString(messageBody);
            messageType = "REGISTRATION";
            dossierHash = SecureHelper.hash_hmac(pass,messageBody);
            String time = "gqlzaWduYXR1cmXaAqxCZHFtTlMycWZQbk1JVm1PY05JWjdQVy9Da1FpZDB4SlFjS20wTzZHNzNuZUYzYlF5R1R0c3hRd0tPV0d5NFEzNnJNK3pQblU4Ri95R0dkRFozQW1JQUV6Tlp2RjRyc2RIZE42SDg4MFJjNG01YzdVQkk1MWhqOXJ4TXRNY0pXZTVQTUpyNFo4dlhPQUcrTmdVQVNScmtHeHBUNDhJTVlCVkpHQWxIZ3FsaVNTeTJoSzdjdDhHZzlkQk1yVFpWcXFLaVVMNkJ6MGthcmVQazA0WjZrZ2hwOEwyS1ovYjBFM0RnSS9mTVlLMDFjNDl3cUxRRExuZWF4a0NFREhtQUUvYlBha3B2eWJYVk5ldmtEaWtKOVpnbzdsdFhhR1BjVG1jTXZoYjFDVUtieHZISlVFRUpXbVFXb0tCTWhwRzJqWlFtWGJCRFZUczBkUEYzUXlqZnhLelJFWVFTWGJRZk85eFNmdTlwckQrUWtRSmRqdHY2Z2RURHJsU3BMaFloZnJDNXkwclA2amVyQnpzVjVmQkNuMGlzZzB2VllVUGM2M3k0ZW45d3R5UHhhcTlTZ3cvZDhLUmgyS2ZuaG4yNi9QSWxtdlYrU1hYbUFNV2c5TDNOcTdVVXBUNHJNKy9WWGZTNzAvUXRyQmVMdkYvUUFmSkhhRDJFK2RLeEV5cndXdHQ1SEQyVWxBOTlUb01YVk15UHhHaFErOS9Ba21QWVpxS2RzekU3Ym9OYjhQTG44S2dGNUMzYkkrajh4VVhsN3d3Rm9YZ0NlV09VSmdmUHJZV1hETUg2WERRQ3dYZzQyMVN6Y3Jpd2k1dWtKOVhmYjgrVXl6d3locWJQcG9mWEFjZnhHRWJLNVVUZ0NGQlZEMkFRNzFPbytmbnVuYURpNGR1UjV4bWVjbE5FWT2pdGltZXN0YW1wsjE1MjEyMTQ5MDguNzAwMjkxMg==";
            Message message = new Message(messageHash.getBytes(),time);
            PmSignature pmSignature = new PmSignature(SecureHelper.sign("test2",SecureHelper.toBytes(message)),time);
            messageSig = SecureHelper.toString(pmSignature);
            sender = "";
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
