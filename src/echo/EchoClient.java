package echo;

import java.io.*;
import java.net.Socket;

public class EchoClient {
  Socket s;

  String ip = "localhost";
  int port = 5000;

  public void init() {
    try {
      s = new Socket(ip, port);

      listen();
      speak();
    } catch (IOException ie) {

    }
  }

  public void listen() {
    Thread th = new Thread() {
      @Override
      public void run() {
        try {
          BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
          PrintWriter pw = new PrintWriter(System.out, true);
          String str = "";
          while ((str = br.readLine()) != null) {
            pw.println(str);
          }
        } catch (IOException ie) {
        }
      }
    };
    th.start();
  }

  public void speak() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
      PrintWriter pw = new PrintWriter(s.getOutputStream());

      pw.println(makeNickname(br));
      pw.flush();
      String str = "";
      while ((str = br.readLine()) != null) {
        pw.println(str);
        pw.flush();
      }

    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

  public String makeNickname(BufferedReader br) throws IOException {
    while (true) {
      System.out.print("사용할 닉네임을 입력해주세요: ");
      String nickname = br.readLine();
      System.out.print(nickname + "을 사용하시겠습니까? [1번, 혹은 예][2번, 혹은 아니요] ");
      String choice = br.readLine();
      switch (choice) {
        case "1", "예" -> {
          System.out.println(nickname + "으로 입장하셨습니다.");
          return nickname;
        }
        case "2", "아니요" -> {
        }

      }
    }
  }

  public static void main(String[] args) {
    EchoClient e = new EchoClient();
    e.init();
  }
}
