package com.cafe24.network.chat.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
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
	
	JTextField text_ipInput = null;
	JTextField text_portInput = null;
	JTextField text_NameInput = null;
	
	JTextField port_makeRoom = null;
	
	public ChatWindows() {
		chatWindows = this;
		
		setTitle("채팅 시작");
		this.setBounds(100, 200, 350, 170);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		panel.setLayout(new BorderLayout());

		JTextField text_ip = new JTextField("접속아이피");
		text_ip.setPreferredSize(new Dimension(70, 30));
		text_ip.setEditable(false);	//수정 못하게
		
		JTextField text_port = new JTextField("접속포트");
		text_port.setPreferredSize(new Dimension(70, 30));
		text_port.setEditable(false);	//수정 못하게
		
		JTextField text_Name = new JTextField("닉네임");
		text_Name.setPreferredSize(new Dimension(70, 30));
		text_Name.setEditable(false);	//수정 못하게
		
		JPanel panel_inname = new JPanel();
		panel_inname.setLayout(new GridLayout(3, 1));
		panel_inname.add(text_ip);
		panel_inname.add(text_port);
		panel_inname.add(text_Name);
		

		Random rand = new Random();
		text_ipInput = new JTextField("127.0.0.1");
		text_portInput = new JTextField("9696");
		text_NameInput = new JTextField("손님"+(rand.nextInt(100)+1));
		
		JPanel panel_ininput = new JPanel();
		panel_ininput.setLayout(new GridLayout(3, 1));
		panel_ininput.add(text_ipInput);
		panel_ininput.add(text_portInput);
		panel_ininput.add(text_NameInput);
		
		
		JButton btn_in = new JButton("입장하기");
		
		
		JPanel panel_in = new JPanel();
		panel_in.setLayout(new BorderLayout());
		panel_in.add(panel_inname, BorderLayout.LINE_START);
		panel_in.add(panel_ininput, BorderLayout.CENTER);
		panel_in.add(btn_in, BorderLayout.LINE_END);
		
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		
		JButton btn_makeRoom = new JButton("서버열기");
		JTextField portText_makeRoom = new JTextField("서버포트 ");
		portText_makeRoom.setPreferredSize(new Dimension(70, 30));
		portText_makeRoom.setEditable(false);	//수정 못하게
		port_makeRoom = new JTextField("9696");


		panel2.add(portText_makeRoom, BorderLayout.LINE_START);
		panel2.add(port_makeRoom, BorderLayout.CENTER);
		panel2.add(btn_makeRoom, BorderLayout.LINE_END);
		

		JPanel panel_blank = new JPanel();
		panel_blank.setLayout(null);
		panel_blank.setPreferredSize(new Dimension(70, 10));

		panel.add(panel_in, BorderLayout.PAGE_START);
		panel.add(panel_blank, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.PAGE_END);
		
		//서버열기 버튼 리스너
		btn_makeRoom.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int tmp_port = -1;
				
				try {
					tmp_port = Integer.parseInt(port_makeRoom.getText());
				}catch(Exception ee) { return; }
				
				if(tmp_port != -1) {
					ChatServer chatServer = new ChatServer(tmp_port);
					chatServer.start();
					chatWindows.dispose();
				}
			}
		});
		
		//입장하기 버튼 리스너
		btn_in.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//공백이 아닐때만 닉네임으로 인정
				String nickname = text_NameInput.getText();
				String tmp_ip = text_ipInput.getText();
				int tmp_port = -1;
				
				try {
					tmp_port = Integer.parseInt(text_portInput.getText());
				}catch(Exception ee) { return; }
				
				if(!"".equals(nickname) && !"".equals(tmp_ip) && tmp_port != -1) {
					ChatClient chatClient = new ChatClient(nickname, tmp_ip, tmp_port);
					chatClient.start();
					chatWindows.dispose();
				}
			}
		});
		
		
		this.add(panel);
		setVisible(true);
	}
}
