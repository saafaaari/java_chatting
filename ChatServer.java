import java.net.*;
import java.text.SimpleDateFormat;
import java.io.*;
import java.util.*;

public class ChatServer {
   Vector clientVector = new Vector(); // 현재 연결된 클라이언트 정보를 유지하고 있는 데이터
   int clientNum = 0; // 접속된 클라이언트의 수
   Hashtable<String, ChatThread> ht = new Hashtable<String, ChatThread>();// 이름을 키값 클라이언트 정보를 벨류값으로 저장하는 헤쉬테이블

   // 접속된 모든 클라이언트에게 메시지 msg 를 보냄

   public void HT(String name, ChatThread id) {// 헤쉬테이블에 정보를 저장할 메소드
      ht.put(name, id);// 헤쉬테이블에 정보 저장
   }

   public void HTC(String Nname, ChatThread id, String name) {// 헤쉬테이블에 정보를 저장할 메소드
      ht.put(Nname, id);// 헤쉬테이블에 정보 저장
      ht.remove(name);// 제거
   }
   public void HTR(String name) {// 헤쉬테이블에 정보를 저장할 메소드
	      
	      ht.remove(name);// 제거
	   }

   public void broadcast(String msg) throws IOException {
      synchronized (clientVector) {
         //////////////// 시간 관련 소스///////////////
         Calendar calendar = Calendar.getInstance();
         java.util.Date date = calendar.getTime();
         String today = (new SimpleDateFormat("     [H시mm분]").format(date));
         //////////////// 시간 관련 소스///////////////

         for (int i = 0; i < clientVector.size(); i++) {
            ChatThread client = (ChatThread) clientVector.elementAt(i);
            synchronized (client) {
               client.sendMessage(msg + today);
            }
         }
      }
   }

   public void unicast(String msg, String name,String me) throws IOException// 귓속말을 보내줄 메소드 생성
   {
      synchronized (clientVector) {
         if (ht.containsKey(name) == true) {
            //////////////// 시간 관련 소스///////////////
            Calendar calendar = Calendar.getInstance();
            java.util.Date date = calendar.getTime();
            String today = (new SimpleDateFormat("    [H시mm분]").format(date));
            //////////////// 시간 관련 소스///////////////

            ChatThread client = ht.get(name);// 귓속말 보낼 클라이언트를 헤쉬테이블에 벨류값으로 구분
            synchronized (client) {
               client.sendMessage("<쪽지가 도착했습니다!>" + msg + today);
            }
         } else {
            
               ChatThread client = ht.get(me);
               synchronized (client) {
                  client.sendMessage("사용자가 없습니다.");
               }
            
         }
      }
   }
   public void Bunicast(String msg, String name) throws IOException// 귓속말을 보내줄 메소드 생성
   {
      synchronized (clientVector) {
         if (ht.containsKey(name) == true) {
            //////////////// 시간 관련 소스///////////////
            Calendar calendar = Calendar.getInstance();
            java.util.Date date = calendar.getTime();
            String today = (new SimpleDateFormat("    [H시mm분]").format(date));
            //////////////// 시간 관련 소스///////////////

            ChatThread client = ht.get(name);// 귓속말 보낼 클라이언트를 헤쉬테이블에 벨류값으로 구분
            synchronized (client) {
               client.sendMessage(msg + today);
            }
         } else {
            for (int i = 0; i < clientVector.size(); i++) {
               ChatThread client = (ChatThread) clientVector.elementAt(i);
               synchronized (client) {
                  client.sendMessage("사용자가 없습니다.");
               }
            }
         }
      }
   }

   // 종료시 clientVector에 저장되어 있는 클라이언트 정보를 제거
   public void removeClient(ChatThread client) {
      synchronized (clientVector) {
         clientVector.removeElement(client);
         client = null;
         System.gc();

      }
   }

   // 처음 연결되었을 때 clientVector에 해당 클라이언트의 정보를 추가
   public void addClient(ChatThread client) {
      synchronized (clientVector) {
         clientVector.addElement(client);
      }
   }

   // 서버의 시작 메인 메소드
   public static void main(String[] args) {
      // 서버 소켓
      ServerSocket myServerSocket = null;

      // ChatServer 객체 생성
      ChatServer myServer = new ChatServer();

      try {
         // 서버 포트 10129를 가지는 서버 소켓 생성
         myServerSocket = new ServerSocket(10129);
      } catch (IOException e) {
         System.out.println(e.toString());
         System.exit(-1);
      }

      System.out.println("[서버 대기 상태] " + myServerSocket);

      try {
         // 다수의 클라이언트 접속을 처리하기 위해 반복문으로 구현
         while (true) {
            // 클라이언트가 접속되었을 경우 이 클라이언트를 처리하기 위한 ChatThread 객체 생성
            ChatThread client = new ChatThread(myServer, myServerSocket.accept());

            // 클라이언트에게 서비스를 제공하기 위한 쓰레드 동작
            client.start();
            // clientVector 에 클라이언트 객체 추가
            // myServer.ht.put(name, myServer.clientNum);
            myServer.addClient(client);

            // 접속한 클라이언트의 수 증가
            myServer.clientNum++;
            System.out.println("[현재 접속자수] " + myServer.clientNum + "명");
         }
      } catch (IOException e) {
         System.out.println(e.toString());
      }
   }
}
