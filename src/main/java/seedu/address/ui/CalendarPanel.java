package seedu.address.ui;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.logging.Logger;

import com.calendarfx.model.Calendar;
import com.calendarfx.model.CalendarSource;
import com.calendarfx.model.Entry;
import com.calendarfx.view.CalendarView;
import com.calendarfx.view.page.PageBase;
import com.google.common.eventbus.Subscribe;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import seedu.address.commons.core.LogsCenter;
import seedu.address.commons.events.model.ScheduleChangedEvent;
import seedu.address.model.ReadOnlySchedule;
import seedu.address.model.lesson.Day;
import seedu.address.model.lesson.Lesson;
import seedu.address.model.lesson.Time;

//@@author demitycho
/**
 * The Calendar Panel of the App.
 */
public class CalendarPanel extends UiPart<CalendarView> {

    private static final String FXML = "CalendarPanel.fxml";
    private static final String DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm";
    private static final String ENTRY_TITLE = "Lesson: ";

    private final Logger logger = LogsCenter.getLogger(this.getClass());

    @javafx.fxml.FXML
    private CalendarView calendarView;
    private Calendar calendar;
    private PageBase pageBase;

    public CalendarPanel(ReadOnlySchedule readOnlySchedule) {
        super(FXML);
        initializeCalendar();
        setUpCalendarView();
        loadEntries(readOnlySchedule);
        updateTime();
        registerAsAnEventHandler(this);
    }

    public CalendarView getCalendarView() {
        return calendarView;
    }

    public PageBase getPageBase() {
        return pageBase;
    }

    /**
     * Initializes the calendar
     */
    private void initializeCalendar() {
        calendar = new Calendar("Lessons");
    }

    /**
     * Sets up the calendar view
     */
    private void setUpCalendarView() {
        CalendarSource calendarSource = new CalendarSource("My Calendar");
        calendarSource.getCalendars().addAll(calendar);

        calendarView.getCalendarSources().addAll(calendarSource);

        calendarView.setRequestedTime(LocalTime.now());
        calendarView.showMonthPage();
        pageBase = calendarView.getSelectedPage();
    }

    /**
     * Loads {@code lessons} into the calendar
     */
    private void loadEntries(ReadOnlySchedule readOnlySchedule) {
        readOnlySchedule.getSchedule().stream().forEach(this::loadEntry);
    }

    /**
     * Creates an entry with the {@code lesson} details and loads it into the calendar
     */
    private void loadEntry(Lesson lesson) {
        String dateString = "10/04/2018";
        String startTimeString = "10:00";
        String endTimeString = "10:30";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_TIME_FORMAT);
        LocalDateTime startDateTime = LocalDateTime.parse(dateString + " " + startTimeString, formatter);
        LocalDateTime endDateTime = LocalDateTime.parse(dateString + " " + endTimeString, formatter);

        Entry entry = new Entry();
        entry.setInterval(startDateTime, endDateTime);
        entry.setTitle(ENTRY_TITLE + lesson.getUniqueKey());
        entry.setCalendar(calendar);
    }


    /**
     * Handles the event where an lesson is deleted by loading the updated lessons list into the calendar
     * @param event contains the updated lessons list
     */
    @Subscribe
    private void handleScheduleChangedEvent(ScheduleChangedEvent event) {
        logger.info(LogsCenter.getEventHandlingLogMessage(event));
        calendar.clear();
        loadEntries(event.getLessons());
    }


    /**
     * Zooms in on the calendar if possible
     */
    private void zoomIn() {
        if (pageBase.equals(calendarView.getYearPage())) {
            calendarView.showMonthPage();
        } else if (pageBase.equals(calendarView.getMonthPage())) {
            calendarView.showWeekPage();
        } else if (pageBase.equals(calendarView.getWeekPage())) {
            calendarView.showDayPage();
        }
        pageBase = calendarView.getSelectedPage();
    }

    /**
     * Zooms out on the calendar if possible
     */
    private void zoomOut() {
        if (pageBase.equals(calendarView.getDayPage())) {
            calendarView.showWeekPage();
        } else if (pageBase.equals(calendarView.getWeekPage())) {
            calendarView.showMonthPage();
        } else if (pageBase.equals(calendarView.getMonthPage())) {
            calendarView.showYearPage();
        }
        pageBase = calendarView.getSelectedPage();
    }

    /**
     * Update the current date and time shown in the calendar as a thread in the background
     * Adapted from http://dlsc.com/wp-content/html/calendarfx/manual.html
     */
    private void updateTime() {
        Thread updateTimeThread = new Thread("Calendar: Update Time Thread") {
            @Override
            public void run() {
                while (true) {
                    Platform.runLater(() -> {
                        calendarView.setToday(LocalDate.now());
                        calendarView.setTime(LocalTime.now());
                    });

                    try {
                        // update every 10 seconds
                        sleep(10000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        updateTimeThread.setPriority(Thread.MIN_PRIORITY);
        updateTimeThread.setDaemon(true);
        updateTimeThread.start();
    }

}