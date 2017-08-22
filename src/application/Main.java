package application;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;


public class Main extends Application {
	private final BooleanProperty dragModeActiveProperty =
			new SimpleBooleanProperty(this, "dragModeActive", true);
	
	@Override
	public void start(Stage primaryStage) {
		
		final Node loginPanel = makeDraggable(createLoginPanel());
		final Node confirmationPanel = makeDraggable(createConfirmationPanel());
		final Node progressPane = makeDraggable(createProgressPanel());
		
		loginPanel.relocate(0, 0);
		confirmationPanel.relocate(0, 100);
		progressPane.relocate(0, 150);
		
		final Pane panelsPane = new Pane();
		panelsPane.getChildren().addAll(loginPanel, confirmationPanel, progressPane);
		final BorderPane sceneRoot = new BorderPane();
		
		BorderPane.setAlignment(panelsPane, Pos.TOP_LEFT);
		sceneRoot.setCenter(panelsPane);
		
		final CheckBox dragModeCheckbox = new CheckBox("Mover Paneles");
		BorderPane.setMargin(dragModeCheckbox, new Insets(6));
		sceneRoot.setBottom(dragModeCheckbox);
		
		dragModeActiveProperty.bind(dragModeCheckbox.selectedProperty());
		
		final Scene scene = new Scene(sceneRoot, 400, 300);
		primaryStage.setScene(scene);
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	private Node makeDraggable(final Node node) {
		final DragContext dragContext = new DragContext();
		final Group wrapGroup = new Group(node);
		
		wrapGroup.addEventFilter(MouseEvent.ANY, 
				(MouseEvent mouseEvent) -> { if(dragModeActiveProperty.get()) 
				{
					mouseEvent.consume();
				}});
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_PRESSED, 
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(final MouseEvent mouseEvent) {
						if(dragModeActiveProperty.get())
						{
							dragContext.mouseAnchorX = mouseEvent.getX();
							dragContext.mouseAnchorY = mouseEvent.getY();
							dragContext.initialTranslateX =
									node.getTranslateX();
							dragContext.initialTranslateY = node.getTranslateY();
						}
					}
				});
		
		wrapGroup.addEventFilter(MouseEvent.MOUSE_DRAGGED,
				new EventHandler<MouseEvent>() {

					@Override
					public void handle(final MouseEvent mouseEvent) {
						if(dragModeActiveProperty.get())
						{
							node.setTranslateX(dragContext.initialTranslateX + mouseEvent.getX() - dragContext.mouseAnchorX);
							node.setTranslateY(dragContext.initialTranslateY + mouseEvent.getY() - dragContext.mouseAnchorY);
						}
						
					}
				});
		
		return wrapGroup;
	}
	
	private static RadioButton createRadioButton(final String text,
			final ToggleGroup toggleGroup,
			final boolean selected)
	{
		final RadioButton radioButton = new RadioButton(text);
		radioButton.setToggleGroup(toggleGroup);
		radioButton.setSelected(selected);
		
		return radioButton;
	}
	
	private static Node createLoginPanel()
	{
		final ToggleGroup toggleGroup = new ToggleGroup();
		
		final TextField textField = new TextField();
		textField.setPrefColumnCount(10);
		textField.setPromptText("Su nombre");
		
		
		final PasswordField passwordField = new PasswordField();
		passwordField.setPrefColumnCount(10);
		passwordField.setPromptText("Su contrasena");
		
		
		final TextField numeroTelefono = new TextField();
		numeroTelefono.setPrefColumnCount(10);
		numeroTelefono.setPromptText("Numero de Telefono");
		
		final ChoiceBox<String> choiceBox = new ChoiceBox<String>(
				FXCollections.observableArrayList("English", "\u0420\u0433\u0441\u0441\u043a\u0438\u0439", "Fran\u00E7ais"));
		choiceBox.setTooltip(new Tooltip("Escoja su idioma"));
		choiceBox.getSelectionModel().select(0);
		
		final Button btnValidar = new Button("Validar");
		
		final HBox panel = new HBox(6, 
				new VBox(2, 
						createRadioButton("Alto", toggleGroup, true), 
						createRadioButton("Medio", toggleGroup, false),
						createRadioButton("Bajo", toggleGroup, false)),
				new VBox(2, textField, passwordField, numeroTelefono),
				choiceBox,
				btnValidar);
		
		btnValidar.setOnAction(e -> {validarExpresionRegular(textField, "(\\w)+|(\\s)+", "Nombre Validado", "Error en el nombre"); 
									validarExpresionRegular(numeroTelefono, "(\\d){10}", "Numero Validado", "Error Numero");});
		
	
		panel.setAlignment(Pos.BOTTOM_LEFT);
		configureBorder(panel);
		
		return panel;
	}
	
	private static boolean validarExpresionRegular(TextField text, String expresionRegular, String msgExito, String msgError)
	{
		String texto = text.getText();
		if(texto.matches(expresionRegular))
		{
			crearPantallaModal(text, msgExito);
		    return true;
		}else {
			crearPantallaModal(text, msgError);
		    return false;
		}
	}

	private static void crearPantallaModal(TextField text, String mensaje) {
		final Stage dialog = new Stage();
		dialog.initModality(Modality.WINDOW_MODAL);
		dialog.initOwner(text.getScene().getWindow());
		dialog.setTitle(mensaje);
		dialog.showAndWait();
	}
	
	private static Node createProgressPanel()
	{
		final Slider slider = new Slider();
		
		final ProgressIndicator progressIndicator = new ProgressIndicator(0);
		
		progressIndicator.progressProperty().bind(
				Bindings.divide(slider.valueProperty(), slider.maxProperty()));
		
		final HBox panel = new HBox(6, new Label("Progreso: "), slider, progressIndicator);
		
		configureBorder(panel);
		
		return panel;
	}
	
	private static Node createConfirmationPanel()
	{
		final Label acceptanceLabel = new Label("No disponible");
		
		final Button acceptButton = new Button("Aceptar");
		
		acceptButton.setOnAction( e -> acceptanceLabel.setText("Aceptado"));
		
		final Button declineButton = new Button("Rechazar");
		
		declineButton.setOnAction( e -> acceptanceLabel.setText("Rechazar"));
		
		final HBox panel = new HBox(6, acceptButton, declineButton, acceptanceLabel);
		
		panel.setAlignment(Pos.CENTER_LEFT);
		configureBorder(panel);
		return panel;
	}
	
	private static void configureBorder(final Region region)
	{
		region.setStyle("-fx-background-color: white;"
				+ "-fx-border-color: black;"
				+ "-fx-border-width: 1;"
				+ "-fx-border-radius: 6;"
				+ "-fx-padding: 6;");
	}
	
	private static final class DragContext{
		public double mouseAnchorX;
		public double mouseAnchorY;
		public double initialTranslateX;
		public double initialTranslateY;
	}
}
