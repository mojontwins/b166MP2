package de.matthiasmann.twl;

import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleDialog {
	private String theme = "simpledialog";
	private String title;
	private Object msg;
	private Runnable cbOk;
	private Runnable cbCancel;

	public void setTheme(String theme) {
		if(theme == null) {
			throw new NullPointerException();
		} else {
			this.theme = theme;
		}
	}

	public String getTitle() {
		return this.title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Object getMessage() {
		return this.msg;
	}

	public void setMessage(Object msg) {
		this.msg = msg;
	}

	public Runnable getOkCallback() {
		return this.cbOk;
	}

	public void setOkCallback(Runnable cbOk) {
		this.cbOk = cbOk;
	}

	public Runnable getCancelCallback() {
		return this.cbCancel;
	}

	public void setCancelCallback(Runnable cbCancel) {
		this.cbCancel = cbCancel;
	}

	public PopupWindow showDialog(Widget owner) {
		if(owner == null) {
			throw new NullPointerException("owner");
		} else {
			Object msgWidget = null;
			PopupWindow popupWindow;
			if(this.msg instanceof Widget) {
				msgWidget = (Widget)this.msg;
				if(((Widget)msgWidget).getParent() instanceof DialogLayout && ((Widget)msgWidget).getParent().getParent() instanceof PopupWindow) {
					popupWindow = (PopupWindow)((Widget)msgWidget).getParent().getParent();
					if(!popupWindow.isOpen()) {
						((Widget)msgWidget).getParent().removeChild((Widget)msgWidget);
					}
				}

				if(((Widget)msgWidget).getParent() != null) {
					throw new IllegalArgumentException("message widget alreay in use");
				}
			} else if(this.msg instanceof String) {
				msgWidget = new Label((String)this.msg);
			} else if(this.msg != null) {
				Logger.getLogger(SimpleDialog.class.getName()).log(Level.WARNING, "Unsupported message type: {0}", this.msg.getClass());
			}

			popupWindow = new PopupWindow(owner);
			Button btnOk = new Button("Ok");
			btnOk.setTheme("btnOk");
			btnOk.addCallback(new SimpleDialog.ButtonCB(popupWindow, this.cbOk));
			Button btnCancel = new Button("Cancel");
			btnCancel.setTheme("btnCancel");
			btnCancel.addCallback(new SimpleDialog.ButtonCB(popupWindow, this.cbCancel));
			DialogLayout layout = new DialogLayout();
			layout.setTheme("content");
			layout.setHorizontalGroup(layout.createParallelGroup());
			layout.setVerticalGroup(layout.createSequentialGroup());
			String vertPrevWidget = "top";
			if(this.title != null) {
				Label labelTitle = new Label(this.title);
				labelTitle.setTheme("title");
				labelTitle.setLabelFor((Widget)msgWidget);
				layout.getHorizontalGroup().addWidget(labelTitle);
				layout.getVerticalGroup().addWidget(labelTitle);
				vertPrevWidget = "title";
			}

			if(msgWidget != null) {
				layout.getHorizontalGroup().addGroup(layout.createSequentialGroup().addGap("left-msg").addWidget((Widget)msgWidget).addGap("msg-right"));
				layout.getVerticalGroup().addGap(vertPrevWidget.concat("-msg")).addWidget((Widget)msgWidget).addGap("msg-buttons");
			} else {
				layout.getVerticalGroup().addGap(vertPrevWidget.concat("-buttons"));
			}

			layout.getHorizontalGroup().addGroup(layout.createSequentialGroup().addGap("left-btnOk").addWidget(btnOk).addGap("btnOk-btnCancel").addWidget(btnCancel).addGap("btnCancel-right"));
			layout.getVerticalGroup().addGroup(layout.createParallelGroup(new Widget[]{btnOk, btnCancel}));
			popupWindow.setTheme(this.theme);
			popupWindow.add(layout);
			popupWindow.openPopupCentered();
			if(msgWidget != null && ((Widget)msgWidget).canAcceptKeyboardFocus()) {
				((Widget)msgWidget).requestKeyboardFocus();
			}

			return popupWindow;
		}
	}

	static class ButtonCB implements Runnable {
		private final PopupWindow popupWindow;
		private final Runnable cb;

		public ButtonCB(PopupWindow popupWindow, Runnable cb) {
			this.popupWindow = popupWindow;
			this.cb = cb;
		}

		public void run() {
			this.popupWindow.closePopup();
			if(this.cb != null) {
				this.cb.run();
			}

		}
	}
}
