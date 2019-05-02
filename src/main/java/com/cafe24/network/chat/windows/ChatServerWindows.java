package com.cafe24.network.chat.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import com.cafe24.network.chat.server.ChatServer;

public class ChatServerWindows extends JFrame {
	JTextArea messages = null;
	JScrollPane scrollpane = null;
	JScrollPane scrollpane2 = null;
	JList<String> jlist = null;
	
	ChatServer chatServer = null;
	
	public ChatServerWindows(ChatServer chatServer) {
		this.chatServer = chatServer;
		
		setTitle("채팅 서버");
		setBounds(100, 200, 350, 200);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());
		
		//메시지리스트
		messages = new JTextArea();
		messages.setEditable(false);
		messages.setText("");
		scrollpane = new JScrollPane();
		//가로스크롤 지우기
		scrollpane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane.setViewportView(messages);
		
		//접속자 리스트
		jlist = new JList<String>();
		scrollpane2 = new JScrollPane();
		scrollpane2.setViewportView(jlist);
		scrollpane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane2.setPreferredSize(new Dimension(80, jlist.getHeight()));
		
		panel.add(scrollpane, BorderLayout.CENTER);
		panel.add(scrollpane2, BorderLayout.LINE_START);
		
		add(panel);
		setVisible(true);
	}
	//윈도우창에서 서버로그 찍기
	public void serverLog(String msg) {
		messages.setText(messages.getText() + "[Server] " + msg + "\n");
		messages.setCaretPosition(messages.getDocument().getLength());
	}
	//사용자 리스트 업데이트
	public void updateJList(String[] list) {
		jlist.setListData(list);
		scrollpane2.setPreferredSize(new Dimension(80, jlist.getHeight()));
	}
}
