package com.cafe24.network.chat.windows;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;

import com.cafe24.network.chat.client.ChatClient;

public class ChatClientWindows extends JFrame {
	ChatClient chatClient = null;
	JTextArea messages = null;
	JTextField msg_input = null;
	JScrollPane scrollpane = null;

	public ChatClientWindows(ChatClient chatClient, String nickname) {
		this.chatClient = chatClient;
		
		setTitle(nickname + ":: 채팅 클라이언트");
		setBounds(100, 200, 300, 200);
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
		
		//메시지입력
		msg_input = new JTextField();
		//보내기버튼
		JButton send_btn = new JButton("전송");
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(msg_input, BorderLayout.CENTER);
		panel2.add(send_btn, BorderLayout.LINE_END);
		

		panel.add(scrollpane, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.PAGE_END);
		
		//보내기버튼 리스너
		send_btn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				//내용받고
				String msg = msg_input.getText();
				//내용지우기
				msg_input.setText("");
				chatClient.sendToServer("message",msg);
			}
		});

		//메시지 입력 후 엔터 눌렀을 때 리스너
		msg_input.addKeyListener(new KeyListener() {
			public void keyTyped(KeyEvent e) {}
			public void keyReleased(KeyEvent e) {}
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyChar() == KeyEvent.VK_ENTER) {
					send_btn.doClick();
				}
			}
		});
		
		//닫기 리스너
		addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowClosed(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {
				chatClient.doQuit();
			}
		});
		
		
		add(panel);
		setVisible(true);
	}

	//JTextPane 업데이트
	public void updateJText(String msg) {
		messages.setText(messages.getText() + msg + "\n");
		messages.setCaretPosition(messages.getDocument().getLength());
	}
}
