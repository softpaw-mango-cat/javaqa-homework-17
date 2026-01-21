package ru.netology;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selenide;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Keys;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;

public class DeliveryFormTest {

    @BeforeAll
    public static void setup() {
        System.setProperty("selenide.holdBrowserOpen", "true");
        System.setProperty("selenide.browser", "chrome");
        System.setProperty("selenide.headless", "true");
    }

    @Test
    public void shouldSendFormWithCorrectData() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateDate());

        $("[data-test-id='name'] input").setValue("Иванова Ольга");
        $("[data-test-id='phone'] input").setValue("+79101111111");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Забронировать"))).click();

        String expectedText = "Встреча успешно забронирована на " + generateDate();

        $("[data-test-id='notification'] .notification__content")
                .should(Condition.appear, Duration.ofSeconds(15))
                .shouldHave(Condition.exactText(expectedText));
    }

    @Test
    public void shouldNotSendFormWithIncorrectCity() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("город");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateDate());

        $("[data-test-id='name'] input").setValue("Иванова Ольга");
        $("[data-test-id='phone'] input").setValue("+79101111111");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Забронировать"))).click();

        String expectedText = "Доставка в выбранный город недоступна";
        $("[data-test-id='city'] .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectDate() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateIncorrectDate());

        $("[data-test-id='name'] input").setValue("Иванова Ольга");
        $("[data-test-id='phone'] input").setValue("+79101111111");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Забронировать"))).click();

        String expectedText = "Заказ на выбранную дату невозможен";
        $("[data-test-id='date'] .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectName() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateDate());

        $("[data-test-id='name'] input").setValue("name");
        $("[data-test-id='phone'] input").setValue("+79101111111");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Забронировать"))).click();

        String expectedText = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
        $("[data-test-id='name'] .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithIncorrectPhone() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateDate());

        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("111111");
        $("[data-test-id='agreement'] .checkbox__box").click();
        $$("button").find((Condition.exactText("Забронировать"))).click();

        String expectedText = "Телефон указан неверно. Должно быть 11 цифр, например, +79012345678.";
        $("[data-test-id='phone'] .input__sub")
                .shouldHave(Condition.exactText(expectedText));

        $("[data-test-id='notification']")
                .shouldNot(Condition.appear);
    }

    @Test
    public void shouldNotSendFormWithoutAgreementCheckbox() {
        Selenide.open("http://localhost:9999");
        $("[data-test-id='city'] input").setValue("Москва");

        $("[data-test-id='date'] input")
                .press(Keys.chord(Keys.SHIFT, Keys.HOME), Keys.DELETE)
                .setValue(generateDate());

        $("[data-test-id='name'] input").setValue("Иванов Иван");
        $("[data-test-id='phone'] input").setValue("+79101111111");
        $$("button").find((Condition.exactText("Забронировать"))).click();

        $("[data-test-id='agreement']")
                .shouldHave(Condition.cssClass("input_invalid"));
        $("[data-test-id='notification']")
                .shouldNot(Condition.appear);
    }

    private String generateDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.plusDays(4).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }

    private String generateIncorrectDate() {
        LocalDate currentDate = LocalDate.now();
        return currentDate.minusDays(2).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
    }
}
