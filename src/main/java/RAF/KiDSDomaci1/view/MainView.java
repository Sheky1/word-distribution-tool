package RAF.KiDSDomaci1.view;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

import RAF.KiDSDomaci1.app.Config;
import RAF.KiDSDomaci1.model.cruncher.Cruncher;
import RAF.KiDSDomaci1.model.Disk;
import RAF.KiDSDomaci1.model.input.FileInput;
import RAF.KiDSDomaci1.model.output.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainView {
    private Stage stage;
    private ComboBox<Disk> disks;
    private HBox left;
    private VBox fileInput, cruncher;
    private Pane center, right;
    private ListView<String> results;
    private Button addFileInput, singleResult, sumResult;
    private ArrayList<FileInputView> fileInputViews;
    private LineChart<Number, Number> lineChart;
    private ArrayList<Cruncher> availableCrunchers;
    private ExecutorService inputThreadPool;
    private ForkJoinPool cruncherThreadPool;
    private ExecutorService outputThreadPool;
    private FileOutput fileOutput;

    private Button addCruncher;

    private static final MainView INSTANCE = new MainView();

    private MainView() {
    }

    ;

    public static MainView getInstance() {
        return INSTANCE;
    }

    public void initMainView(BorderPane borderPane, Stage stage) {

        this.stage = stage;

        fileInputViews = new ArrayList<FileInputView>();
        availableCrunchers = new ArrayList<Cruncher>();

        left = new HBox();

        borderPane.setLeft(left);

        initFileInput();

        initCruncher();

        initCenter(borderPane);

        initRight(borderPane);
    }

    private void initFileInput() {
        fileInput = new VBox();

        fileInput.getChildren().add(new Text("File inputs:"));
        VBox.setMargin(fileInput.getChildren().get(0), new Insets(0, 0, 10, 0));

        disks = new ComboBox<Disk>();
        disks.getSelectionModel().selectedItemProperty().addListener(e -> updateEnableAddFileInput());
        disks.setMinWidth(120);
        disks.setMaxWidth(120);
        fileInput.getChildren().add(disks);

        addFileInput = new Button("Add FileInput");
        addFileInput.setOnAction(e -> addFileInput(new FileInput(disks.getSelectionModel().getSelectedItem())));
        VBox.setMargin(addFileInput, new Insets(5, 0, 10, 0));
        addFileInput.setMinWidth(120);
        addFileInput.setMaxWidth(120);
        fileInput.getChildren().add(addFileInput);

        int width = 210;

        VBox divider = new VBox();
        divider.getStyleClass().add("divider");
        divider.setMinWidth(width);
        divider.setMaxWidth(width);
        fileInput.getChildren().add(divider);
        VBox.setMargin(divider, new Insets(0, 0, 15, 0));

        Insets insets = new Insets(10);
        ScrollPane scrollPane = new ScrollPane(fileInput);
        scrollPane.setMinWidth(width + 35);
        fileInput.setPadding(insets);
        fileInput.getChildren().add(scrollPane);

        left.getChildren().add(scrollPane);


        try {
            String[] disksArray = Config.getProperty("disks").split(";");
            for (String disk : disksArray) {
                File file = new File(disk);
                if (!file.exists() || !file.isDirectory()) {
                    throw new Exception("Bad directory path");
                }
                disks.getItems().add(new Disk(file));
            }
            inputThreadPool = Executors.newFixedThreadPool(disksArray.length);
            cruncherThreadPool = ForkJoinPool.commonPool();
            outputThreadPool = Executors.newCachedThreadPool();
            fileOutput = new FileOutput();
            Thread fileOutputThread = new Thread(fileOutput);
            fileOutputThread.start();
            if (disksArray.length > 0) {
                disks.getSelectionModel().select(0);
            }
        } catch (Exception e) {
            Platform.runLater(new Runnable() {

                @Override
                public void run() {
                    Alert alert = new Alert(AlertType.ERROR);
                    alert.setTitle("Closing");
                    alert.setHeaderText("Bad config disks");
                    alert.setContentText(null);

                    alert.showAndWait();
                    System.exit(0);
                }
            });
        }

        updateEnableAddFileInput();
    }

    private void initCruncher() {
        cruncher = new VBox();

        Text text = new Text("Crunchers");
        cruncher.getChildren().add(text);
        VBox.setMargin(text, new Insets(0, 0, 5, 0));

        addCruncher = new Button("Add cruncher");
        addCruncher.setOnAction(e -> addCruncher());
        cruncher.getChildren().add(addCruncher);
        VBox.setMargin(addCruncher, new Insets(0, 0, 15, 0));

        int width = 110;

        Insets insets = new Insets(10);
        ScrollPane scrollPane = new ScrollPane(cruncher);
        scrollPane.setMinWidth(width + 35);
        cruncher.setPadding(insets);
        left.getChildren().add(scrollPane);
    }

    private void initCenter(BorderPane borderPane) {
        center = new HBox();

        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Bag of words");
        yAxis.setLabel("Frequency");
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setMinWidth(700);
        lineChart.setMinHeight(600);
//		XYChart.Series series = new XYChart.Series();
//		series.setName("My portfolio");
        //populating the series with data
//		series.getData().add(new XYChart.Data(1, 23));
//		series.getData().add(new XYChart.Data(2, 14));
//		series.getData().add(new XYChart.Data(3, 15));
//		series.getData().add(new XYChart.Data(4, 24));
//		series.getData().add(new XYChart.Data(5, 34));
//		series.getData().add(new XYChart.Data(6, 36));
//		series.getData().add(new XYChart.Data(7, 22));
//		series.getData().add(new XYChart.Data(8, 45));
//		series.getData().add(new XYChart.Data(9, 43));
//		series.getData().add(new XYChart.Data(10, 17));
//		series.getData().add(new XYChart.Data(11, 29));
//		series.getData().add(new XYChart.Data(12, 25));
//		lineChart.getData().add(series);
        center.getChildren().add(lineChart);

        borderPane.setCenter(center);
    }

    private void initRight(BorderPane borderPane) {
        right = new VBox();
        right.setPadding(new Insets(10));
        right.setMaxWidth(200);

        results = new ListView<String>();
        right.getChildren().add(results);
        VBox.setMargin(results, new Insets(0, 0, 10, 0));
        results.getSelectionModel().selectedItemProperty().addListener(e -> updateResultButtons());
        results.getSelectionModel().selectedIndexProperty().addListener(e -> updateResultButtons());
        results.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        singleResult = new Button("Single result");
        singleResult.setOnAction(e -> getSingleResult());
        singleResult.setDisable(true);
        right.getChildren().add(singleResult);
        VBox.setMargin(singleResult, new Insets(0, 0, 5, 0));

        sumResult = new Button("Sum results");
        sumResult.setDisable(true);
        sumResult.setOnAction(e -> sumResults());
        right.getChildren().add(sumResult);
        VBox.setMargin(sumResult, new Insets(0, 0, 10, 0));

        borderPane.setRight(right);
    }

    public void updateEnableAddFileInput() {
        Disk disk = disks.getSelectionModel().getSelectedItem();
        if (disk != null) {
            for (FileInputView fileInputView : fileInputViews) {
                if (fileInputView.getFileInput().getDisk() == disk) {
                    addFileInput.setDisable(true);
                    return;
                }
            }
            addFileInput.setDisable(false);
        } else {
            addFileInput.setDisable(true);
        }
    }

    public void updateResultButtons() {
        if (results.getSelectionModel().getSelectedItems() == null || results.getSelectionModel().getSelectedItems().size() == 0) {
            singleResult.setDisable(true);
            sumResult.setDisable(true);
        } else if (results.getSelectionModel().getSelectedItems().size() == 1) {
            singleResult.setDisable(false);
            sumResult.setDisable(true);
        } else {
            singleResult.setDisable(true);
            sumResult.setDisable(false);
        }
    }

    private void getSingleResult() {
        Map<String, Integer> selectedJob;
        selectedJob = fileOutput.poll(results.getSelectionModel().getSelectedItem());
        if(selectedJob == null) {
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("The job isn't done crunching!");

            alert.showAndWait();
            return;
        }
        Future<Map<String, Integer>> sortedJobFuture = outputThreadPool.submit(new SingleOutputWorker(selectedJob));
        drawGraph(sortedJobFuture);
    }

    private void sumResults() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Set name");
        dialog.setHeaderText("Set name for this sum");

        Optional<String> result = dialog.showAndWait();
        if(result.isEmpty()) return;
        String sumName = result.get();

        Future<Map<String, Integer>> allJobsFuture = outputThreadPool.submit(new SumOutputWorker());
        Map<String, Integer> allJobs = null;
        try {
            allJobs = allJobsFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        Future<Map<String, Integer>> sortedJobFuture = outputThreadPool.submit(new SingleOutputWorker(allJobs));
        drawGraph(sortedJobFuture);
        fileOutput.getFutures().put(sumName, sortedJobFuture);
        Platform.runLater(() -> {
            results.getItems().add(sumName);
        });
        Map<String, Future<Map<String, Integer>>> jobToDo = new HashMap<>();
        jobToDo.put(sumName, sortedJobFuture);
        Thread resultGetterThread = new Thread(new ResultGetter(jobToDo, null, fileOutput));
        resultGetterThread.start();

    }

    public void drawGraph(Future<Map<String,Integer>> sortedJobFuture) {
        try {
            Map<String, Integer> sortedJob = sortedJobFuture.get();
            int i = 0;
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            for (Map.Entry<String, Integer> entry : sortedJob.entrySet()) {
                if (i == 100) {
                    break;
                }
                i++;
                series.getData().add(new XYChart.Data<>(i, entry.getValue()));
            }

            Platform.runLater(() -> {
                lineChart.getData().clear();
                lineChart.getData().addAll(series);
            });
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void addFileInput(FileInput fileInput) {
        FileInputView fileInputView = new FileInputView(fileInput);
        fileInput.setFileInputView(fileInputView);
        this.fileInput.getChildren().add(fileInputView.getFileInputView());
        VBox.setMargin(fileInputView.getFileInputView(), new Insets(0, 0, 30, 0));
        fileInputView.getFileInputView().getStyleClass().add("file-input");
        fileInputViews.add(fileInputView);
        if (availableCrunchers != null) {
            fileInputView.updateAvailableCrunchers(availableCrunchers);
        }
        updateEnableAddFileInput();
    }

    public void removeFileInputView(FileInputView fileInputView) {
        fileInput.getChildren().remove(fileInputView.getFileInputView());
        fileInputViews.remove(fileInputView);
        updateEnableAddFileInput();
    }

    public void updateCrunchers(ArrayList<Cruncher> crunchers) {
        for (FileInputView fileInputView : fileInputViews) {
            fileInputView.updateAvailableCrunchers(crunchers);
        }
        this.availableCrunchers = crunchers;
    }

    public Stage getStage() {
        return stage;
    }

    public LineChart<Number, Number> getLineChart() {
        return lineChart;
    }

    public void setLineChart(LineChart<Number, Number> lineChart) {
        this.lineChart = lineChart;
    }

    private void addCruncher() {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add cruncher");
        dialog.setHeaderText("Enter cruncher arity");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(res -> {
            try {
                int arity = Integer.parseInt(res);
                for (Cruncher cruncher : availableCrunchers) {
                    if (cruncher.getArity() == arity) {
                        Alert alert = new Alert(AlertType.WARNING);
                        alert.setTitle("Error");
                        alert.setHeaderText("Cruncher with this arity already exists.");
                        alert.setContentText(null);
                        alert.showAndWait();
                        return;
                    }
                }
                Cruncher cruncher = new Cruncher(arity);
                fileOutput.getCrunchers().add(cruncher);
                CruncherView cruncherView = new CruncherView(cruncher);
                this.cruncher.getChildren().add(cruncherView.getCruncherView());
                cruncher.setCruncherView(cruncherView);
                availableCrunchers.add(cruncher);
                updateCrunchers(availableCrunchers);
            } catch (NumberFormatException e) {
                Alert alert = new Alert(AlertType.ERROR);
                alert.setTitle("Wrong input");
                alert.setHeaderText("Arity must be a number");
                alert.showAndWait();
            }
        });
    }

    public void removeCruncher(CruncherView cruncherView) {
        for (FileInputView fileInputView : fileInputViews) {
            fileInputView.removeLinkedCruncher(cruncherView.getCruncher());
        }
        availableCrunchers.remove(cruncherView.getCruncher());
        updateCrunchers(availableCrunchers);
        cruncher.getChildren().remove(cruncherView.getCruncherView());
    }

    public void outOfMemory() {
        inputThreadPool.shutdown();
        cruncherThreadPool.shutdown();
        outputThreadPool.shutdown();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("Memory error. System is closing");

        alert.showAndWait();

        System.exit(0);
    }

    public void quitApplication() {
        inputThreadPool.shutdown();
        cruncherThreadPool.shutdown();
        outputThreadPool.shutdown();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The system is closing");

        alert.showAndWait();

        System.exit(0);
    }

    public void closeWindowEvent(WindowEvent event) {
        inputThreadPool.shutdown();
        cruncherThreadPool.shutdown();
        outputThreadPool.shutdown();

        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText("The system is closing");

        alert.showAndWait();

        System.exit(0);

    }

    public Pane getRight() {
        return right;
    }

    public ExecutorService getInputThreadPool() {
        return inputThreadPool;
    }

    public void setInputThreadPool(ExecutorService inputThreadPool) {
        this.inputThreadPool = inputThreadPool;
    }

    public ForkJoinPool getCruncherThreadPool() {
        return cruncherThreadPool;
    }

    public void setCruncherThreadPool(ForkJoinPool cruncherThreadPool) {
        this.cruncherThreadPool = cruncherThreadPool;
    }

    public ExecutorService getOutputThreadPool() {
        return outputThreadPool;
    }

    public void setOutputThreadPool(ExecutorService outputThreadPool) {
        this.outputThreadPool = outputThreadPool;
    }

    public FileOutput getFileOutput() {
        return fileOutput;
    }

    public ListView<String> getResults() {
        return results;
    }
}
