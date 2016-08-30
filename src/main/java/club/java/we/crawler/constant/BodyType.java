package club.java.we.crawler.constant;

public enum BodyType {

	BINARY("binary"), TEXT("text");

	private String val;

	private BodyType(String type) {
		this.val = type;
	}

	public String val() {
		return this.val;
	}

}
