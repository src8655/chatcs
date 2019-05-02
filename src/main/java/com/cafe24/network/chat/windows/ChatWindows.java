package com.cafe24.network.chat.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.cafe24.network.chat.client.ChatClient;
import com.cafe24.network.chat.server.ChatServer;

public class ChatWindows extends JFrame {
	public ChatWindows chatWindows = null;
	JTextField text_NameInput = null;
	
	public ChatWindows() {
		chatWindows = this;
		
		setTitle("채팅 시작");
		this.setBounds(100, 200, 350, 100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JButton btn_makeRoom = new JButton("서버열기");
		JTextField text_Name = new JTextField("  닉네임  ");
		text_Name.setEditable(false);	//수정 못하게
		Random rand = new Random();
		text_NameInput = new JTextField("손님"+(rand.nextInt(100)+1));
		JButton btn_in = new JButton("입장하기");

		panel.add(text_Name, BorderLayout.LINE_START);
		panel.add(text_NameInput, BorderLayout.CENTER);
		panel.add(btn_in, BorderLayout.LINE_END);
		panel.add(btn_makeRoom, BorderLayout.PAGE_END);
		
		//서버열기 버튼 리스너
		btn_makeRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ChatServer chatServer = new ChatServer();
				chatServer.start();
				chatWindows.dispose();
			}
		});
		
		//입장하기 버튼 리스너
		btn_in.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//공백이 아닐때만 닉네임으로 인정
				String nickname = text_NameInput.getText();
				if(!"".equals(nickname)) {
					ChatClient chatClient = new ChatClient(nickname);
					chatClient.start();
					chatWindows.dispose();
				}
			}
		});
		
		
		this.add(panel);
		setVisible(true);
	}
}
