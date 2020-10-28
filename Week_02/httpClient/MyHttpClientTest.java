package greekTime.week2;

public class MyHttpClientTest {
    public static void main(String[] args) {
        MyHttpClient client = new MyHttpClient();
        String response = client.get("http://127.0.0.1:8801");
        System.out.println(response);
    }
}
