package com.github.tasnim2001.tasnimintelliji.keywords;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.psi.PsiFile;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
public class KeywordLoader {
    public static Map<String, Object> loadKeywords() {

        // Load the file from resources
        InputStream inputStream = KeywordLoader.class
                .getClassLoader()
                .getResourceAsStream("keywords.json"); // opens the file
          // If the JSON file is missing , stop everything and show an exception
        if (inputStream == null) {
            throw new RuntimeException("keywords.json  is not found !");
        }

        Type mapType = new TypeToken<Map<String, Object>>() {}.getType();
        return new Gson().fromJson(new InputStreamReader(inputStream), mapType);
    }
}



/*
{
  "authentication": [
    "login",
    "authenticate",
    "authn",
    "password",
    "pwd",
    "credential",
    "creds",
    "otp",
    "mfa",
    "twofactor",
    "sso",
    "token",
    "jwt",
    "x509",
    "cert",
    "keystore"
  ],

  "authorization": [
    "role",
    "rbac",
    "abac",
    "permit",
    "deny",
    "privilege",
    "accessControl",
    "accessManager",
    "policy",
    "scope"
  ],

  "encryption": [
    "encrypt",
    "decrypt",
    "cipher",
    "aes",
    "rsa",
    "sha",
    "md5",
    "key",
    "keystore",
    "keypair",
    "publicKey",
    "privateKey"
  ],

  "validation": [
    "validate",
    "validator",
    "sanitize",
    "sanitizeInput",
    "sanitizeOutput",
    "escapeHtml",
    "preparedStatement"
  ],

  "session": [
    "session",
    "timeout",
    "sessionId",
    "sessionToken"
  ]
}*/

