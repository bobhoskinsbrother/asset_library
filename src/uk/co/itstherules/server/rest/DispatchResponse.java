package uk.co.itstherules.server.rest;

public final class DispatchResponse {

    private final int status;
    private final String contentType;
    private final String body;

    public DispatchResponse(int status, String contentType, String body) {
        this.status = status;
        this.contentType = contentType;
        this.body = body;
    }

    public DispatchResponse(String contentType, String body) {
        this(200, contentType, body);
    }

    public DispatchResponse(String body) {
        this(200, "application/json", body);
    }

    public int getStatus() {
        return status;
    }

    public String getContentType() {
        return contentType;
    }

    public String getBody() {
        return body;
    }
}
