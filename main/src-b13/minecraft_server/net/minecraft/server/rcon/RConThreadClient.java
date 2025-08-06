package net.minecraft.server.rcon;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;

import net.minecraft.server.IServer;

public class RConThreadClient extends RConThreadBase {
	private boolean loggedIn = false;
	private Socket clientSocket;
	private byte[] buffer = new byte[1460];
	private String rconPassword;

	RConThreadClient(IServer iServer1, Socket socket2) {
		super(iServer1);
		this.clientSocket = socket2;
		this.rconPassword = iServer1.getStringProperty("rcon.password", "");
		this.log("Rcon connection from: " + socket2.getInetAddress());
	}

	public void run() {
		while(true) {
			try {
				if(!this.running) {
					break;
				}

				try {
					BufferedInputStream bufferedInputStream1 = new BufferedInputStream(this.clientSocket.getInputStream());
					int i2 = bufferedInputStream1.read(this.buffer, 0, 1460);
					if(10 <= i2) {
						byte b3 = 0;
						int i4 = RConUtils.getBytesAsLEInt(this.buffer, 0, i2);
						if(i4 != i2 - 4) {
							return;
						}

						int i21 = b3 + 4;
						int i5 = RConUtils.getBytesAsLEInt(this.buffer, i21, i2);
						i21 += 4;
						int i6 = RConUtils.getRemainingBytesAsLEInt(this.buffer, i21);
						i21 += 4;
						switch(i6) {
						case 2:
							if(this.loggedIn) {
								String string8 = RConUtils.getBytesAsString(this.buffer, i21, i2);

								try {
									this.sendMultipacketResponse(i5, this.server.handleRConCommand(string8));
								} catch (Exception exception16) {
									this.sendMultipacketResponse(i5, "Error executing: " + string8 + " (" + exception16.getMessage() + ")");
								}
								continue;
							}

							this.sendLoginFailedResponse();
							continue;
						case 3:
							String string7 = RConUtils.getBytesAsString(this.buffer, i21, i2);
							if(0 != string7.length() && string7.equals(this.rconPassword)) {
								this.loggedIn = true;
								this.sendResponse(i5, 2, "");
								continue;
							}

							this.loggedIn = false;
							this.sendLoginFailedResponse();
							continue;
						default:
							this.sendMultipacketResponse(i5, String.format("Unknown request %s", new Object[]{Integer.toHexString(i6)}));
							continue;
						}
					}
				} catch (SocketTimeoutException socketTimeoutException17) {
					continue;
				} catch (IOException iOException18) {
					if(this.running) {
						this.log("IO: " + iOException18.getMessage());
					}
					continue;
				}
			} catch (Exception exception19) {
				System.out.println(exception19);
				break;
			} finally {
				this.closeSocket();
			}

			return;
		}

	}

	private void sendResponse(int i1, int i2, String string3) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream4 = new ByteArrayOutputStream(1248);
		DataOutputStream dataOutputStream5 = new DataOutputStream(byteArrayOutputStream4);
		dataOutputStream5.writeInt(Integer.reverseBytes(string3.length() + 10));
		dataOutputStream5.writeInt(Integer.reverseBytes(i1));
		dataOutputStream5.writeInt(Integer.reverseBytes(i2));
		dataOutputStream5.writeBytes(string3);
		dataOutputStream5.write(0);
		dataOutputStream5.write(0);
		this.clientSocket.getOutputStream().write(byteArrayOutputStream4.toByteArray());
	}

	private void sendLoginFailedResponse() throws IOException {
		this.sendResponse(-1, 2, "");
	}

	private void sendMultipacketResponse(int i1, String string2) throws IOException {
		int i3 = string2.length();

		do {
			int i4 = 4096 <= i3 ? 4096 : i3;
			this.sendResponse(i1, 0, string2.substring(0, i4));
			string2 = string2.substring(i4);
			i3 = string2.length();
		} while(0 != i3);

	}

	private void closeSocket() {
		if(null != this.clientSocket) {
			try {
				this.clientSocket.close();
			} catch (IOException iOException2) {
				this.logWarning("IO: " + iOException2.getMessage());
			}

			this.clientSocket = null;
		}
	}
}
