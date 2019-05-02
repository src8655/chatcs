package com.cafe24.network.chat.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
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
	JScrollPane scrollpane2 = null;
	JList<String> jlist = null;
	

	public ChatClientWindows(ChatClient chatClient, String nickname) {
		this.chatClient = chatClient;
		
		setTitle(nickname + ":: 채팅 클라이언트");
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
		
		//메시지입력
		msg_input = new JTextField();
		//보내기버튼
		JButton send_btn = new JButton("전송");
		
		JPanel panel2 = new JPanel();
		panel2.setLayout(new BorderLayout());
		panel2.add(msg_input, BorderLayout.CENTER);
		panel2.add(send_btn, BorderLayout.LINE_END);
		
		//접속자 리스트
		jlist = new JList<String>();
		scrollpane2 = new JScrollPane();
		scrollpane2.setViewportView(jlist);
		scrollpane2.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrollpane2.setPreferredSize(new Dimension(80, jlist.getHeight()));
		
		
		panel.add(scrollpane, BorderLayout.CENTER);
		panel.add(panel2, BorderLayout.PAGE_END);
		panel.add(scrollpane2, BorderLayout.LINE_END);
		
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
		//더블클릭 시 귓속말
		jlist.addMouseListener(new MouseListener() {
			public void mouseReleased(MouseEvent e) {}
			public void mousePressed(MouseEvent e) {}
			public void mouseExited(MouseEvent e) {}
			public void mouseEntered(MouseEvent e) {}
			public void mouseClicked(MouseEvent e) {
				JList<String> list = (JList<String>)e.getSource();
				
				//더블클릭 시
				if(e.getClickCount() == 2) {
					//선택한 이름과 스레드id를 찾는다
					int index = list.locationToIndex(e.getPoint());
					String tmp_NickName = chatClient.arrayNickname[index];
					String tmp_Id = chatClient.arrayId[index];
					updateJTextField("To." + tmp_NickName + "(" + tmp_Id + "):");	//입력창 수정
				}
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
	//사용자 리스트 업데이트
	public void updateJList(String[] list) {
		jlist.setListData(list);
		scrollpane2.setPreferredSize(new Dimension(80, jlist.getHeight()));
	}
	//입력창 수정
	public void updateJTextField(String msg) {
		msg_input.setText(msg);
	}
}
