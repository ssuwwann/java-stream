package echo;


import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ConcurrentHashMap;

public class EchoServer {
  ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();

  int port = 5000;

  public EchoServer() {
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
    String nickname = "";
    User user = null;
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
      PrintWriter pw = new PrintWriter(System.out, true);

      nickname = br.readLine();
      user = new User(nickname, "suwan", s);
      userMap.put(nickname, user);

      System.out.println(nickname + "님이 입장했습니다.");
    } catch (IOException ie) {
      ie.printStackTrace();
    }

    listen(user);
    speak(user);
  }

  public void listen(User user) {
    Thread th = new Thread() {
      public void run() {
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(user.getSocket().getInputStream()));
          PrintWriter pw = new PrintWriter(System.out, true);
          String str = "";
          while ((str = br.readLine()) != null) {
            pw.println(user.getUsername() + ">> " + str);
          }
        } catch (SocketException se) {
          System.out.println(user.getUsername() + "님이 퇴장했습니다.");
        } catch (IOException ie) {
          ie.printStackTrace();
        }
      }
    };
    th.start();
  }

  public void speak(User user) {
    Thread th = new Thread() {
      @Override
      public void run() {
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
          PrintWriter pw = new PrintWriter(user.getSocket().getOutputStream(), true);

          String str = "";
          while ((str = br.readLine()) != null) {
            pw.println("운영자>> " + str);
          }

        } catch (IOException ie) {
          ie.printStackTrace();
        }
      }
    };
    th.start();
  }

  public static void main(String[] args) {
    EchoServer e = new EchoServer();
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