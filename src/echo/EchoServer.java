package echo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EchoServer {
  public static final int PORT = 7196;

  public static void main(String[] args) {
    ServerSocket ss = null;
    List<EchoServerThread> userList = Collections.synchronizedList(new ArrayList());

    try {
      ss = new ServerSocket();
      ss.bind(new InetSocketAddress("0.0.0.0", PORT));
      System.out.println("[SERVER] Listening on PORT [" + PORT + "]");

      new Thread(new EchoServerInput(userList)).start();
      while (true) {
        Socket s = ss.accept();
        new Thread(new EchoServerThread(s, userList)).start();
      }
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}

class EchoServerThread implements Runnable {
  Socket s;
  private List<EchoServerThread> userList;
  private String nickname;

  EchoServerThread(Socket s, List<EchoServerThread> userList) {
    this.s = s;
    this.userList = userList;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf-8"));
      getNickname(br);

      String msg = "";
      while ((msg = br.readLine()) != null) {
        notifyAllClients(nickname + ">> " + msg);
      }

    } catch (SocketException se) {
      se.printStackTrace();
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  void getNickname(BufferedReader br) {
    try {
      nickname = br.readLine();
      userList.add(this);
      System.out.println(nickname + "님 입장\t\t현재 인원: " + userList.size());
      notifyAllClients(nickname + "님 입장하셨습니다.");
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  void notifyAllClients(String msg) {
    System.out.println(msg);
    try {
      for (EchoServerThread t : userList) {
        PrintWriter out = new PrintWriter(new OutputStreamWriter(t.s.getOutputStream(), "utf-8"), true);
        out.println(msg);
      }
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }
}

class EchoServerInput implements Runnable {
  List<EchoServerThread> userList;

  EchoServerInput(List<EchoServerThread> userList) {
    this.userList = userList;
  }

  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

      String msg = "";
      while ((msg = br.readLine()) != null) {
        for (EchoServerThread t : userList) {
          PrintWriter out = new PrintWriter(new OutputStreamWriter(t.s.getOutputStream(), "utf-8"), true);
          out.println("서버: " + msg);
        }
      }
    } catch (IOException ie) {
    }
  }
}