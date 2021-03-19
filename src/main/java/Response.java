package main.java;

import java.util.HashMap;

public class Response {
    private String status;
    private HashMap<String, String> res;

    public Response(HashMap<String, String> res, String status) {
        this.res = res;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Response{" +
                "status='" + status + '\'' +
                ", res=" + res +
                '}';
    }
}
