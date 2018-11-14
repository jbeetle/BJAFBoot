import java.io.IOException;

import com.beetle.component.boot.BJAFBoot;

public class TestClient2 extends BJAFBoot {

	@Override
	protected int getPort() {
		return 9090;
	}

	public static void main(String[] args) {
		try {
			new TestClient2().start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
