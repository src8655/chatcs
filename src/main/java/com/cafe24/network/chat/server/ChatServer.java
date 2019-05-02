package com.cafe24.network.chat.server;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import com.cafe24.network.chat.windows.ChatServerWindows;

public class ChatServer extends Thread {
	private static final int PORT = 9696;

	HashMap<Long, ChatServerThread> socketThreadMap = null;
	ServerSocket serverSocket = null;
	
	ChatServerWindows chatServerWindows = null;

	public void run() {
		//윈도우창 오픈
		chatServerWindows = new ChatServerWindows(this);
		
		try {
			socketThreadMap = new HashMap<Long, ChatServerThread>();	//연결된 목록을 저장하는 맵
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
		finally {
			try {
				if(serverSocket != null && !serverSocket.isClosed()) serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			} 
		}
	}
	

	//모든 연결된 소켓에 뿌리기
	public void broadCast(String msg) throws IOException {
		synchronized(socketThreadMap) {
			Set<Long> set = socketThreadMap.keySet();
			Iterator<Long> it = set.iterator();
			while(it.hasNext()) {
				Long key = it.next();
				ChatServerThread ct  = socketThreadMap.get(key);
				ct.send(msg);
			}
		}
	}
	//join 메시지 띄우기(본인 제외한 다른사람들에게 보내기)
	public void broadCastJoin(String msg, Long id) throws IOException {
		synchronized(socketThreadMap) {
			Set<Long> set = socketThreadMap.keySet();
			Iterator<Long> it = set.iterator();
			while(it.hasNext()) {
				Long key = it.next();
				if(key == id) continue;
				ChatServerThread ct  = socketThreadMap.get(key);
				ct.send(msg);
			}
			broadCastMember();
		}
	}
	//귓속말 보내기(보낸사람과 받는사람만 보내기)
	public void broadCastSecret(String from_nickname, Long from_id, String to_msg, String to_nickname, Long to_id) throws IOException {
		synchronized(socketThreadMap) {
			//보내는사람 소켓을 담은 스레드
			ChatServerThread from_cct = socketThreadMap.get(from_id);
			//보내는사람이 존재하는지 또는 맞는지 닉네임을 비교(유효성검사)
			if(from_cct == null || !from_nickname.equals(from_cct.nickname)) return;	//보내는사람이 없어지면 그냥종료
			
			//받는사람 소켓을 담은 스레드
			ChatServerThread to_cct = socketThreadMap.get(to_id);
			//받는사람이 존재하는지 또는 맞는지 닉네임을 비교(유효성검사)
			if(to_cct == null || !to_nickname.equals(to_cct.nickname)) {
				from_cct.send(encode("상대방이 없습니다."));	//받는사람이 없어지면 보낸사람한테 오류메시지 보내기
				return;
			}

			//둘다 존재하면 정상처리
			from_cct.send(encode("To."+to_nickname+"("+to_id+"):"+to_msg));
			//둘다 존재하면 정상처리
			to_cct.send(encode("From."+from_nickname+"("+from_id+"):"+to_msg));
		}
	}
	//연결된 소켓 리스트에 추가
	public void addSocketThreadList(ChatServerThread chatServerThread) {
		synchronized(socketThreadMap) {
			socketThreadMap.put(chatServerThread.getId(), chatServerThread);
		}
	}
	//연결 해제된 소켓 리스트에서 제거
	public void removeSocketThreadList(ChatServerThread chatServerThread) {
		synchronized(socketThreadMap) {
			socketThreadMap.remove(chatServerThread.getId());
			broadCastMember();
		}
	}
	//접속자를 방송
	public void broadCastMember() {
		synchronized(socketThreadMap) {
			String[] list_NickNames = new String[socketThreadMap.size()];
			
			Set<Long> set = socketThreadMap.keySet();
			Iterator<Long> it = set.iterator();
			String msg = null;
			int i = 0;
			while(it.hasNext()) {
				Long key = it.next();
				ChatServerThread ct  = socketThreadMap.get(key);
				
				if(msg == null) msg = "";
				else msg += "/";
				
				msg  = msg + key + ":" + ct.nickname;
				
				list_NickNames[i] = ct.nickname;
				i++;
			}
			serverLog("연결소켓 개수(" + socketThreadMap.size() + ")");
			//serverLog("연결리스트 : " + msg);
			//서버에도 리스트 갱신
			chatServerWindows.updateJList(list_NickNames);
			
			try {
				if(msg != null) broadCast("member@@" + encode(msg));
			} catch (IOException e) {
				e.printStackTrace();
			}
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
	//서버로그 찍기
	public void serverLog(String msg) {
		System.out.println("[Server] " + msg);
		chatServerWindows.serverLog(msg);
	}

}
