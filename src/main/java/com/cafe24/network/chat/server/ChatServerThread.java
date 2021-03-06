package com.cafe24.network.chat.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.util.Base64;


public class ChatServerThread extends Thread {
    BufferedReader r = null;
	PrintWriter pr = null;
    ChatServer server = null;
	Socket socket = null;
	
	String nickname = "NoName";
	
    ChatServerThread(ChatServer server, Socket socket) {
    	this.server = server;
    	this.socket = socket;
    }

	@Override
	public void run() {
		super.run();
		
		try {
			r = new BufferedReader(new InputStreamReader(socket.getInputStream(), "utf-8"));
			pr = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "utf-8"), true);
			
			while(true) {
				String msg = r.readLine();					//메시지받기 대기
				String[] tokens = msg.split(":");			//명령어와 분리
				
				String token = encode(" ");					//그냥 엔터 시 공백
				if(tokens.length == 2) token = tokens[1];
				
				//join명령어
				if("join".equals(tokens[0])) doJoin(token);
				//message 명령어
				else if("message".equals(tokens[0])) doMessage(token);
				else if("quit".equals(tokens[0])) doQuit();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			doQuit();
		}
	}
	
	//입장(join) 명령어
	public void doJoin(String nickname) throws IOException {
		//디코드
		nickname = new String(Base64.getDecoder().decode(nickname.getBytes("UTF-8")),"utf-8");
		this.nickname = nickname;
		String msg = encode(nickname+"님이 입장 하였습니다.");
		server.broadCastJoin(msg, this.getId());		//본인 제외 다른사람들한테만 보내기
		send(encode("접속완료 즐거운 채팅 되세요"));		//본인한테만 보내기
	}
	//메시지(message) 명령어
	public void doMessage(String message) throws IOException {
		message = decode(message);	//디코드
		
		//귓속말인지 확인(To.로 시작하는 메시지인지 확인)
		if(message.matches("To.*")) {
			message = message.split("To.")[1];	//To제거
			
			//받는사람 정보
			String tmp_NickName = "";
			Long tmp_Id = 0L;
			
			String[] tmp = null;
			
			tmp = message.split("\\(");
			if(tmp.length < 2) return;
			
			tmp_NickName = tmp[0];
			message = tmp[1];
			
			tmp = message.split("\\):");
			if(tmp.length < 1) return;
			try {
				tmp_Id = Long.parseLong(tmp[0]);
			}catch(Exception e) {
				return;
			}
			if(tmp.length < 2) 
				message = " ";
			else message = tmp[1];
			
			server.broadCastSecret(this.nickname, this.getId(), message, tmp_NickName, tmp_Id);
		}else {
			//귓속말이 아닐 때 전체방송
			String msg = encode(nickname + " : " + message);
			server.broadCast(msg);
		}
	}
	//종료(quit) 명령어
	public void doQuit() {
		try {
			//소켓 리스트에서 삭제
			server.removeSocketThreadList(this);
			server.broadCast(encode(nickname+"님이 퇴장 하였습니다."));
			
			//소켓닫기
			if(pr != null) pr.close();
			if(r != null) r.close();
			if(socket != null && !socket.isClosed()) socket.close();
			this.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	//클라이언트에 메시지 보내기
	public void send(String msg) throws IOException {
		pr.println(msg);
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
