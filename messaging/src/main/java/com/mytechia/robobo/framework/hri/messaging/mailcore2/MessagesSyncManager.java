package com.mytechia.robobo.framework.hri.messaging.mailcore2;


import com.libmailcore.ConnectionType;
import com.libmailcore.IMAPFetchMessagesOperation;
import com.libmailcore.IMAPSession;
import com.libmailcore.IMAPMessage;

/**
 * Created by hoa on 1/8/15.
 */
public class MessagesSyncManager {
    public IMAPSession session;
    public IMAPMessage currentMessage;

    static private MessagesSyncManager theSingleton;

    public static MessagesSyncManager singleton() {
        if (theSingleton == null) {
            theSingleton = new MessagesSyncManager();
        }
        return theSingleton;
    }

    private MessagesSyncManager() {
        session = new IMAPSession();
        session.setUsername("--------");
        session.setPassword("---------");
        session.setHostname("imap.gmail.com");
        session.setPort(993);
        session.setConnectionType(ConnectionType.ConnectionTypeTLS);
    }


}