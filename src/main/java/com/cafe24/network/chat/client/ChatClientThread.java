package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Base64;


public class ChatClientThread extends Thread {
    BufferedReader r = null;
    ChatClient client = null;
    
	ChatClientThread(BufferedReader r, ChatClient client) {
		this.r = r;
		this.client = client;
	}
	@Override
	public void run() {
		super.run();
		
		try {
			while(true) {
				//메시지를 받을 때까지 기다림
				String iMessage = r.readLine();
				//디코드 해서 받는다.
				iMessage = new String(Base64.getDecoder().decode(iMessage.getBytes("UTF-8")),"utf-8");
				client.updateJText(iMessage);	//JTextPane 업데이트
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
