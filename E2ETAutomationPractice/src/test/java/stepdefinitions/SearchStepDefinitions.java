package stepdefinitions;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.abilities.BrowseTheWeb;
import net.serenitybdd.screenplay.ensure.Ensure;
import net.serenitybdd.screenplay.rest.abilities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Get;
import net.thucydides.core.annotations.Managed;
import net.thucydides.core.annotations.Steps;
import org.junit.platform.commons.logging.LoggerFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import screenplay.api_interface.SearchAPI;
import screenplay.questions.CurrentSearchResultCount;
import screenplay.tasks.LookForProductItem;
import screenplay.tasks.NavigateTo;
import org.apache.log4j.LogManager;


import java.util.List;

import static net.serenitybdd.screenplay.rest.questions.ResponseConsequence.seeThatResponse;

public class SearchStepDefinitions {



    private static final Logger LOGGER=(Logger) LoggerFactory.getLogger(SearchStepDefinitions.class);


    @Steps
    SearchAPI searchAPI;

    Actor actor = Actor.named("sudesimsek");
    @Managed
    WebDriver theBrowser;

    @Given("I open browser and go to the main page")
    public void iOpenBrowserAndGoToTheMainPage() {
        actor.can(BrowseTheWeb.with(theBrowser));
        actor.wasAbleTo(NavigateTo.theAutomationPracticeHomePage());
        LOGGER.info("browser and main page are opened");


    }

    @When("I search with {string} in the home page")
    public void iSearchWithInTheHomePage(String term) {
        term = "sude";
        actor.attemptsTo(LookForProductItem.about(term));
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("search is succesfully done ",term);

    }

    @Then("api endpoint should be called with {string}")
    public void apiEndpointShouldBeCalledWith(String term) {
        actor.whoCan(CallAnApi.at(searchAPI.invoke_my_webservice()));
        actor.attemptsTo(
                Get.resource("index.php?controller=search&q=test&limit=10&timestamp=1639907643890&ajaxSearch=1&id_lang=1")
        );

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("api endpoint called");

    }

    @When("api returned related results")
    public void apiReturnedRelatedResults() {
        actor.should(
                seeThatResponse("related results should be retrieved",
                        response -> response.statusCode(2000))
        );
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("api returned related results");

    }

    @Then("I should see related results on the main page")
    public void iShouldSeeRelatedResultsOnTheMainPage() {

        System.out.println("last response: " + SerenityRest.lastResponse().statusCode());
        List<String> resultList = SerenityRest.lastResponse().getBody().jsonPath().getList("");
        System.out.println("resultList: " + resultList.size());
        actor.attemptsTo(
                Ensure.that(CurrentSearchResultCount.information())
                        .contains(resultList.size() + " results have been found.")
        );
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        LOGGER.info("results seen on the main page");
    }
}