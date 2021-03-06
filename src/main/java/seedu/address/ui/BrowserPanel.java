package seedu.address.ui;

import java.net.URL;
import java.util.logging.Logger;

import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.layout.Region;
import javafx.scene.web.WebView;
import seedu.address.MainApp;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.StudentInfoDisplayEvent;
import seedu.address.commons.events.ui.StudentPanelSelectionChangedEvent;
import seedu.address.model.student.Address;
import seedu.address.model.student.Student;

/**
 * The Browser Panel of the App.
 */
public class BrowserPanel extends UiPart<Region> {

    public static final String DEFAULT_PAGE = "default.html";
    public static final String EXAMPLE_STUDENT_PAGE = "ExampleStudentPage.html";
    public static final String SEARCH_PAGE_URL =
            "https://www.google.com.sg/maps/place/";

    private static final String FXML = "BrowserPanel.fxml";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @FXML
    private WebView browser;

    public BrowserPanel() {
        super(FXML);

        // To prevent triggering events for typing inside the loaded Web page.
        getRoot().setOnKeyPressed(Event::consume);

        loadDefaultPage();
        registerAsAnEventHandler(this);
    }

    private void loadStudentPage(Student student) {
        Address location = student.getAddress();
        String append = location.urlstyle();
        loadPage(SEARCH_PAGE_URL + append);
    }

    private void loadStudentInfoPage(Student student) {
        URL exampleStudentPage = MainApp.class.getResource(FXML_FILE_FOLDER + EXAMPLE_STUDENT_PAGE);
        loadPage(exampleStudentPage.toExternalForm());
    }


    public void loadPage(String url) {
        Platform.runLater(() -> browser.getEngine().load(url));
    }

    /**
     * Loads a default HTML file with a background that matches the general theme.
     */
    private void loadDefaultPage() {
        URL defaultPage = MainApp.class.getResource(FXML_FILE_FOLDER + DEFAULT_PAGE);
        loadPage(defaultPage.toExternalForm());
    }

    /**
     * Frees resources allocated to the browser.
     */
    public void freeResources() {
        browser = null;
    }

    @Subscribe
    private void handleStudentPanelSelectionChangedEvent(StudentPanelSelectionChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadStudentPage(event.getNewSelection().student);
    }

    @Subscribe
    private void handleStudentInfoDisplayEvent(StudentInfoDisplayEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        loadStudentInfoPage(event.getStudent());
    }
}
