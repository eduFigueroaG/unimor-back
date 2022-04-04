package mx.edu.utez.unimor.response;

import java.util.HashMap;
import java.util.Map;

public class ApiResponse {

    public Map<String, Object> returnResponse(int option, Object data) {
        Map<String, Object> response = new HashMap<>();
        switch (option) {
            case 1:
                response.put("status", true);
                response.put("data", data);
                break;
            case 2:
                response.put("status", false);
                response.put("message", data);
                break;
            case 3:
                response.put("status", false);
                response.put("error", data);
                break;
            case 4:
                response.put("status", true);
                response.put("auth", data);
                break;
            default:
                return null;
        }
        return response;
    }
}