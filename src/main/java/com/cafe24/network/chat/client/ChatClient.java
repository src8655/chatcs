package com.cafe24.network.chat.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Base64;

import com.cafe24.network.chat.windows.ChatClientWindows;


public class ChatClient extends Thread {
	private static final String SERVER_IP = "127.0.0.1";
	private static final int SERVER_PORT = 9696;

    BufferedReader r = null;
	PrintWriter pr = null;
    Socket socket = null;
    
    ChatClientThread chatClientThread = null;	//클라이언트 메시지받는 스레드
    ChatClientWindows chatClientWindows = null;	//윈도우

	String nickname = null;
	boolean endflag = false;	//종료플래그
	
	//닉네임 저장 생성자
	public ChatClient(String nickname) {
		this.nickname = nickname;
	}
	
	public void run() {
		//윈도우창 오픈
		chatClientWindows = new ChatClientWindows(this, nickname);
		
	    try {
	    	clientLog("접속 대기중...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(SERVER_IP, SERVER_PORT));

			r = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			
			//시작시 닉네임 변경 후 접속
			sendToServer("join", nickname);
			
			//클라이언트 스레드 시작
			chatClientThread = new ChatClientThread(r, this);
			chatClientThread.start();
			
			//종료되지 않게 반복
			while(!endflag) {}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//종료시 정리
	public void doQuit() {
		sendToServer("quit"," ");		//종료신호 보내기
		chatClientThread.stop();		//스레드종료
		
		//소켓닫기
		try {
			if(pr != null) pr.close();
			if(r != null) r.close();
			if(socket != null && !socket.isClosed()) socket.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
		endflag = true;	//종료플래그
	}
	//서버로 전송
	public void sendToServer(String command, String msg) {
		try {
			//인코드해서 보냄
			msg = new String(Base64.getEncoder().encode(msg.getBytes("UTF-8")), "UTF-8");
			pr.println(command + ":" + msg);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	//JTextPane 업데이트
	public void updateJText(String msg) {
		chatClientWindows.updateJText(msg);
	}
	//클라이언트 로그 찍기
	public static void clientLog(String msg) {
		System.out.println("[Client] " + msg);
	}

}
