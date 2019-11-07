package com.zzg.mybatis.generator;

import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.zzg.mybatis.generator.controller.MainUIController;
import com.zzg.mybatis.generator.util.ConfigHelper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 这是本软件的主入口,要运行本软件请直接运行本类就可以了,不用传入任何参数
 */
@SuppressWarnings(value = { "restriction" })
public class MainUI extends Application {

	public static void main(String[] args) {
		String version = System.getProperty("java.version");
		if (Integer.parseInt(version.substring(2, 3)) >= 8 && Integer.parseInt(version.substring(6)) >= 60) {
			launch(args);
		} else {
			JFrame jFrame = new JFrame("版本错误");
			jFrame.setSize(500, 100);
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			JPanel jPanel = new JPanel();
			JLabel jLabel = new JLabel("JDK的版本不能低于1.8.0.60，请升级至最近的JDK 1.8再运行此软件");
			jPanel.add(jLabel);
			jFrame.add(jPanel);
			jFrame.setLocationRelativeTo(null);
			jFrame.setVisible(true);

		}
	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		ConfigHelper.createEmptyFiles();
		URL url = Thread.currentThread().getContextClassLoader().getResource("fxml/MainUI.fxml");
		FXMLLoader fxmlLoader = new FXMLLoader(url);
		Parent root = fxmlLoader.load();
		primaryStage.setResizable(true);
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		MainUIController controller = fxmlLoader.getController();
		controller.setPrimaryStage(primaryStage);
	}
}
