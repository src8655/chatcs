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
	private String server_ip = "1.0.0.1";
	private int server_port = -1;

    BufferedReader r = null;
	PrintWriter pr = null;
    Socket socket = null;
    
    ChatClientThread chatClientThread = null;	//클라이언트 메시지받는 스레드
    ChatClientWindows chatClientWindows = null;	//윈도우

	String nickname = null;
	boolean endflag = false;	//종료플래그

	public String[] arrayNickname = null;
	public String[] arrayId = null;
	
	//닉네임 저장 생성자
	public ChatClient(String nickname, String server_ip, int server_port) {
		this.nickname = nickname;
		this.server_ip = server_ip;
		this.server_port = server_port;
	}
	
	public void run() {
		//윈도우창 오픈
		chatClientWindows = new ChatClientWindows(this, nickname);
		
	    try {
	    	clientLog("접속 대기중...");
			socket = new Socket();
			socket.connect(new InetSocketAddress(server_ip, server_port));

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
			updateJText("서버에 연결할 수 없음.");
			//e.printStackTrace();
		}
	}
	//종료시 정리
	public void doQuit() {
		sendToServer("quit"," ");		//종료신호 보내기
		if(chatClientThread != null) chatClientThread.stop();		//스레드종료
		
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
	public void sendToServer(String command, String msg)  {
		try {
			//인코드해서 보냄
			msg = new String(Base64.getEncoder().encode(msg.getBytes("UTF-8")), "UTF-8");
			if(pr != null) pr.println(command + ":" + msg);
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
	//받아온 멤버리스트 저장하기
	public void setArrayNickname(String msg) {
		String[] list = msg.split("/");		//리스트파싱
		int size = list.length;				//리스트개수 파악
		
		arrayNickname = new String[size];	//사이즈만큼 배열 설정
		arrayId = new String[size];
		
		//하나씩 분리해서 넣기
		for(int i=0;i<size;i++) {
			String[] tokens = list[i].split(":");
			arrayId[i] = tokens[0];
			arrayNickname[i] = tokens[1];
		}
		
		//리스트에 반영
		chatClientWindows.updateJList(arrayNickname);
	}

}
