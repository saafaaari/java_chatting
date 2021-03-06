import java.net.*;
import java.io.*;
import java.applet.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.util.*;
import java.util.Random;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.DefaultCaret;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

public class ChatClient extends Applet implements ActionListener, Runnable {
   Socket mySocket = null;
   PrintWriter out = null;
   BufferedReader in = null;
   int t = 1;
   String n="";
   List list;
   TextField serverIp;
   Button connect;
   Thread clock;
   JTextArea memo;
   TextField name;
   TextField cm;
   Button namechange;
   Button unicast;
   Button allC;
   Button Enter;
   Button Logout;
   JButton Bot;
   JButton Bot1;
   TextField input;
   Panel upPanel, downPanel;
   String Cname;

   Random ran = new Random();
   int i = 0;
   int bc = 0;

   public void init() {
      // GUI
      setLayout(new BorderLayout());
      setSize(700, 700);
      // 텍스트 에어리어 보더레이아웃의 중앙에 위치

      // 패널 생성하여 패널에 IP 주소 입력을 위한 텍스트필드와 연결 버튼 추가

      upPanel = new Panel();
      upPanel.setBackground(Color.pink);
      serverIp = new TextField(12);
      serverIp.setText("서버 IP 주소 입력");
      upPanel.add(serverIp);
      name = new TextField(8);
      name.setText("대화명");
      upPanel.add(name);
      allC = new Button("전체말");
      allC.addActionListener(this);
      unicast = new Button("귓속말");
      unicast.addActionListener(this);
      namechange = new Button("변경");
      namechange.addActionListener(this);
      list = new List();
      Enter = new Button("전송");
      Enter.addActionListener(this);
      Logout = new Button("나가기");
      Logout.addActionListener(this);
      upPanel.add(allC);
      upPanel.add(unicast);
      upPanel.add(namechange);
      connect = new Button("연결");
      connect.addActionListener(this);
      upPanel.add(connect);

      // 생성된 패널을 보더레이아웃의 위쪽에 위치
      add("North", upPanel);
      // 패널 생성하여 대화명을 위한 텍스트필드와 입력을 위한 텍스트필드 추가

      downPanel = new Panel();
      downPanel.setBackground(Color.pink);
      cm = new TextField(8);
      Bot = new JButton("봇 시스템");
      Bot.addActionListener(this);
      downPanel.add(Bot);
      Bot1 = new JButton("봇 종료");
      Bot1.addActionListener(this);
      downPanel.add(Bot1);
      Bot1.setVisible(false);
      cm.setText("TALK");
      downPanel.add(cm);
      input = new TextField(25);
      input.addActionListener(this); // 사용자가 엔터키를 누르면 메시지가 전송되도록 이벤트 연결
      downPanel.add(input);

      downPanel.add(Enter);
      downPanel.add(Logout);
      Cname = name.getText();
      add("South", downPanel); // 보더레이아웃의 아래쪽에 패널 위치

      /** 이미지가 포함된 textarea **/
      memo = new JTextArea(10, 55);
      memo.setBackground(Color.PINK);
      /*
       * {
       * 
       * { setOpaque(false); } // 그림을 표시하게 설정,투명하게 조절
       * 
       * public void paintComponent(Graphics g) { g.drawImage(img, 0, 0, null); //
       * 이미지나 그림을 그린다. super.paintComponent(g); } };
       */
      add(memo, BorderLayout.EAST);
      add(list, BorderLayout.WEST);
      /** 이미지가 포함된 textarea **/

      /** 스크롤링 **/
      JScrollPane jsp = new JScrollPane(memo, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
      add(jsp, BorderLayout.CENTER);

      DefaultCaret caret = (DefaultCaret) memo.getCaret();
      caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
      /** 스크롤링 **/

      setSize(500, 800);
   }

   public void run() // 쓰레드 부분으로 상대방이 보내는 메시지를 받기 위해 while을 돌면서 기다림
   {
      out.println("LOGIN|" + name.getText() + mySocket); // 초기 서버에 접속한 후 이름과 소캣을 내보낸다.
      memo.append("[접속하였습니다]" + "\n");
      out.println("접속자|ㅇㅇ");// 내 화면의 텍스트에어리어 memo에 접속메시지 출력
      try {
         while (true) { // 반복문
            String msg = in.readLine();
            String tok = msg;// 상대방이 보낸 메시지를 읽어들임
            if (!msg.equals("") && !msg.equals(null)) {
               if (tok.charAt(0) == '-' && tok.charAt(1) == '-' && t == 1) {// 접속했을때 현재 접속해있는 사람들을 리스트에 추가(딱 한번만 실행
                                                               // t)토큰 사용
                  String a = tok.substring(3, tok.indexOf("]"));
                  StringTokenizer st = new StringTokenizer(a, ", ");
                  int count = st.countTokens();
                  for (int i = 0; i < count; i++) {
                     String token = st.nextToken();
                     list.add(token);

                  }
                  t -= 1;
               } else if (tok.charAt(0) == '~'&&tok.charAt(1) == '~' && t == 0) {// 추가로 들어오는 사람 리스트에 추가
                  list.add(tok.substring(2, tok.indexOf("님")));
               } else if (tok.charAt(0) == '<' && tok.charAt(1) == '<' && t == 0) {// 나가는 사람 리스트에서 제거

                  list.remove(tok.substring(2, tok.indexOf("님")));

               } else if (tok.charAt(0) == '[' && tok.charAt(1) == '[' && t == 0) {// 이름 변경시 리스트이름도 변경
                  list.add(tok.substring(tok.indexOf(">") + 3, tok.indexOf("(") - 2));
                  list.remove(tok.substring(2, tok.indexOf("]")));

               } else {

               }
               memo.append(msg + "\n"); // 내 화면의 memo에 받은 메시지 출력

            }

         }
      } catch (IOException e) {
         memo.append(e.toString() + "\n");
      }
   }

   public void actionPerformed(ActionEvent e) // connect 버튼이 눌린 경우와 input 텍스트필드에
   { // 엔터가 들어왔을 경우 실행

      if (e.getSource() == connect) { // connect 버튼이 눌렸을 경우 서버에 연결
         try {
            mySocket = new Socket(serverIp.getText(), 10129);// 입력받은 서버 IP 주소와 포트번호

            // 생성된 소켓을 이용해 서버와의 입출력 스트림을 생성
            out = new PrintWriter(new OutputStreamWriter(mySocket.getOutputStream()));
            in = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));

            // 쓰레드를 동작시킴
            if (clock == null) {
               clock = new Thread(this);
               clock.start();
            }
         } catch (UnknownHostException ie) {
            System.out.println(ie.toString());
         } catch (IOException ie) {
            System.out.println(ie.toString());
         }

      } else if (e.getSource() == namechange) {
         String Nname = name.getText();
         out.println("change|[[" + Nname.substring(0, Nname.indexOf(">")) + "] > ["
               + Nname.substring(Nname.indexOf(">") + 1) + "]](으)로 이름을 변경하였습니다.");
         // 변경버튼을 누르면 000(전 이름)>XXX(후 이름) 으로 변경하였습니다 라는 문구를 아웃해준다.
         name.setText(Nname.substring(Nname.indexOf(">") + 1));// 변경버튼을 누르면 이름칸에 새로운 이름으로 바꾸어준다
      } else if (e.getSource() == unicast) {/// 귓속말이 체킹되었을 때
         cm.setText("W>");
      } else if (e.getSource() == allC) {/// 전체말이 체킹되었을 때
         cm.setText("TALK");
      } else if (e.getSource() == Logout) { // 로그아웃 버튼 클릭시 발동
         JOptionPane.showMessageDialog(null, "서버가 종료됩니다");
         out.println("LOGOUT|" + name.getText() + mySocket); // 초기 서버에 접속한 후 이름과 소캣을 내보낸다.
         out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
      }

      else if (e.getSource() == Bot && i == 0) {
         memo.append("============= 봇 시스템이 활성화 되었습니다 ============= \n"
         		+ "봇 기능\n"
         		+ "1.로또번호 추첨기능\n"
         		+ "2.식사메뉴 추첨기능\n"
         		+ "3.노래 불러주기\n");
         out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
         String data = input.getText();
         cm.setText("봇");
         Bot.setText("봇 종료");
         i++;
         try {
            File theFile = new File("\\\\Mac\\Home\\Desktop\\봇 음성파일\\jammin_intro2.wav");
            FileInputStream fis = new FileInputStream(theFile);
            AudioStream as = new AudioStream(fis);
            AudioPlayer.player.start(as);
         } catch (Exception ex) {
            System.out.println(ex);
         }

      } else if (e.getSource() == Bot && i == 1) {
         memo.append("============= 봇 시스템이 비활성화 되었습니다 ============= \n");
         out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
         cm.setText("TALK");

         Bot.setText("봇 시스템");
         i--;
         try {
            File theFile = new File("\\\\Mac\\Home\\Desktop\\봇 음성파일\\jammin_bye.wav");
            FileInputStream fis = new FileInputStream(theFile);
            AudioStream as = new AudioStream(fis);
            AudioPlayer.player.start(as);
         } catch (Exception ex) {
            System.out.println(ex);
         }

      } else if (e.getSource() == input) { // input 텍스트필드에 엔터가 입력될 경우

         String data = input.getText();
         String cmm = cm.getText();// input 텍스프필드의 값을 읽어서

         input.setText("");
         if (data.contains("바보") || data.contains("멍청이") || data.contains("비속어")) {
            if (bc <= 3) {
               bc++;
               memo.append("[시스템]  : 현재 적절하지 못한 단어를 사용하셨습니다." + bc + " 차 경고 \n");
               if (bc == 3) {
                  out.println("LOGOUT1|" + name.getText() + mySocket); // 초기 서버에 접속한 후 이름과 소캣을 내보낸다.
                  out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
                  bc = 0;
               }
            }

         } else {
            // 형식에 맞춰 서버에 메시지를 전송
            out.println(cmm + "|" + "[" + name.getText() + "]" + " : " + data);// 커맨드 부분을 분리해주었다.
            out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
         }
      } else if (e.getSource() == Enter) { // 엔터 효과랑 동일
         String data = input.getText();
         String cmm = cm.getText();// input 텍스프필드의 값을 읽어서

         input.setText("");

         if (data.contains("바보") || data.contains("멍청이") || data.contains("비속어")) {
            if (bc <= 3) {
               bc++;
               memo.append("[시스템]  : 현재 적절하지 못한 단어를 사용하셨습니다." + bc + " 차 경고 \n");
               if (bc == 3) {
                  out.println("LOGOUT1|" + name.getText() + mySocket); // 초기 서버에 접속한 후 이름과 소캣을 내보낸다.
                  out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
                  bc = 0;
               }
            }

         } else {
            // 형식에 맞춰 서버에 메시지를 전송
        	 
            out.println(cmm + "|" + "[" + name.getText() + "]" + " : " + data);// 커맨드 부분을 분리해주었다.
            out.flush(); // 버퍼에 있는 출력 메시지를 상대방에게 강제로 전송
         }
      }
      list.addItemListener(new ItemListener() {
         @Override
         public void itemStateChanged(ItemEvent e) {
            cm.setText("W>" + list.getItem((int) e.getItem()));
         }
      });

   }

   public void stop() // 쓰레드를 종료시키고 종료 메시지를 서버에 전송하고 모든 연결을 닫음
   {
      if ((clock != null) && (clock.isAlive())) {
         clock = null; // 쓰레드 종료
      }

      // 서버에 종료 메시지 보냄
      out.println("LOGOUT|" + name.getText());
      out.flush();

      // 모든 스트림과 소켓 연결을 끊음
      try {
         out.close();
         in.close();
         mySocket.close();
      } catch (IOException e) {
         memo.append(e.toString() + "\n");
      }
   }
}
