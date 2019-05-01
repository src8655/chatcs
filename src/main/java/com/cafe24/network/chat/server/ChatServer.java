package com.cafe24.network.chat.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import com.cafe24.network.chat.windows.ChatServerWindows;

public class ChatServer extends Thread {
	private static final int PORT = 9696;
	
	ArrayList<ChatServerThread> socketThreadList = null;
	ServerSocket serverSocket = null;
	
	ChatServerWindows chatServerWindows = null;

	public void run() {
		//윈도우창 오픈
		chatServerWindows = new ChatServerWindows(this);
		
		try {
			socketThreadList = new ArrayList<ChatServerThread>();		//연결된 목록을 저장하는 리스트
			serverSocket = new ServerSocket();							//서버소켓생성
			serverSocket.bind(new InetSocketAddress("0.0.0.0", PORT));	//아이피+포트설정
		
			while(true) {
				//연결을 기다린다
				serverLog("연결 대기중(" + PORT + ")...");
				Socket socket = serverSocket.accept();
				serverLog("연결 성공(" + socket.getPort() + ")...");
				
				
				ChatServerThread chatServerThread = new ChatServerThread(this, socket);	//스레드객체에 클라이언트 소켓을 전달
				addSocketThreadList(chatServerThread);									//서버리스트에 추가
				chatServerThread.start();												//스레드시작
			}
		} catch (IOException e) {e.printStackTrace();}
	}
	

	//모든 연결된 소켓에 뿌리기
	public void broadCast(String msg) throws IOException {
		synchronized(socketThreadList) {
			for(ChatServerThread ct : socketThreadList){
				ct.send(msg);
	        }
		}
	}
	//join 메시지 띄우기(본인 제외한 다른사람들에게 보내기)
	public void broadCastJoin(String msg, ChatServerThread chatServerThread) throws IOException {
		synchronized(socketThreadList) {
			for(ChatServerThread ct : socketThreadList){
				if(ct != chatServerThread) ct.send(msg);
	        }
		}
	}
	//연결된 소켓 리스트에 추가
	public void addSocketThreadList(ChatServerThread chatServerThread) {
		synchronized(socketThreadList) {
			socketThreadList.add(chatServerThread);
			serverLog("연결소켓 리스트(" + socketThreadList.size() + ")");
		}
	}
	//연결 해제된 소켓 리스트에 제거
	public void removeSocketThreadList(ChatServerThread chatServerThread) {
		synchronized(socketThreadList) {
			socketThreadList.remove(chatServerThread);
			serverLog("연결소켓 리스트(" + socketThreadList.size() + ")");
		}
	}
	//서버로그 찍기
	public void serverLog(String msg) {
		System.out.println("[Server] " + msg);
		chatServerWindows.serverLog(msg);
	}

}
