package echo;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class EchoClient {
  private static final String STRING_SERVER_IP = "localhost";

  public static void main(String[] args) {
    Socket s = null;

    try {
      s = new Socket();
      s.connect(new InetSocketAddress(STRING_SERVER_IP, EchoServer.PORT));

      BufferedReader br = new BufferedReader(new InputStreamReader(System.in, "utf-8"));
      PrintWriter out = new PrintWriter(new OutputStreamWriter(s.getOutputStream(), "utf-8"), true);

      System.out.print("닉네임을 입력해주세요 >> ");
      String nickname = br.readLine();
      out.println(nickname);

      // 듣기
      new Thread(new EchoClientThread(s)).start();

      String msg = "";
      while ((msg = br.readLine()) != null) {
        out.println(msg);
      }
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }

}

class EchoClientThread implements Runnable {
  Socket s;

  EchoClientThread(Socket s) {
    this.s = s;
  }


  @Override
  public void run() {
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream(), "utf-8"));
      String msg = "";
      while ((msg = br.readLine()) != null) {
        System.out.println(msg);
      }
    } catch (SocketException se) {
      System.out.println("서버가 종료되었습니다.");
      System.exit(0);
    } catch (IOException ie) {
      ie.printStackTrace();
    }
  }
}