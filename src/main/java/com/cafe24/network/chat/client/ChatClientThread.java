package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
				
				//사용자 리스트를 받는 메시지일 경우
				String[] tokens = iMessage.split("@@");
				if(tokens.length == 2 && "member".equals(tokens[0])) {
					client.setArrayNickname(decode(tokens[1]));
					continue;
				}
				
				//디코드 해서 받는다.
				iMessage = decode(iMessage);
				client.updateJText(iMessage);	//JTextPane 업데이트
			}
		} catch (IOException e) {
			client.updateJText("서버와 연결이 끊어졌습니다.");
		}
	}
	//base64 인코딩
	public String encode(String str) {
		try {
			str =  new String(Base64.getEncoder().encode(str.getBytes("UTF-8")), "UTF-8");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return str;
	}
	//base64 디코딩
	public String decode(String str) {
		try {
			str =  new String(Base64.getDecoder().decode(str.getBytes("UTF-8")),"utf-8");
		} catch (UnsupportedEncodingException e) {e.printStackTrace();}
		return str;
	}

}
