package echo;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class EchoServer {
  ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

  int port = 5000;
  int i = 0;


  public void init() {
    try {
      ServerSocket ss = new ServerSocket(port);
      System.out.println(port + "번 대기중");
      while (true) {
        Socket s = ss.accept();
        handleClient(s);
      }
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }


  public void handleClient(Socket s) {

    Thread th = new Thread() {
      @Override
      public void run() {
        listen(s);
      }
    };

    th.start();
    speak(s);
  }

  public void listen(Socket s) {
    String nickname = "";
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
      PrintWriter pw = new PrintWriter(System.out, true);

      nickname = br.readLine();
      userMap.put(nickname, new User(nickname, "suwan", s));
      System.out.println(nickname + "님이 입장했습니다.");

      String str = "";
      while ((str = br.readLine()) != null) {
        pw.println(nickname + ">> " + str);
      }
    } catch (SocketException se) {
      System.out.println(nickname + "님이 퇴장했습니다.");
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  public void speak(Socket s) {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter pw = new PrintWriter(s.getOutputStream(), true);

      String str = "";
      while ((str = br.readLine()) != null) {
        pw.println("운영자>> " + str);
      }

    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  public static void main(String[] args) {
    EchoServer e = new EchoServer();
    e.init();
  }
}

class User {
  private String nickname;
  private String password;

  private Socket socket;

  User(String nickname, String password, Socket socket) {
    this.nickname = nickname;
    this.password = password;
    this.socket = socket;
  }

  public String getUsername() {
    return nickname;
  }

  public Socket getSocket() {
    return socket;
  }

  @Override
  public String toString() {
    return "[" + nickname + ", " + password + "]";
  }
}