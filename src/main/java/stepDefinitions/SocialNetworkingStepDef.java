package stepDefinitions;

import com.jayway.jsonpath.DocumentContext;
import cucumber.api.java8.En;
import io.restassured.response.Response;
import utilities.RequestBodyService;

import static org.hamcrest.CoreMatchers.equalTo;
//import static org.hamcrest.CoreMatchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SocialNetworkingStepDef extends BaseSteps implements En {
    Response responseForServiceCall, responseForGetPostCall, responseForGetCommentCall, responseForPostCall, responseForCommentCall;
    RequestBodyService requestBodyService;
    DocumentContext requestBody;

    public SocialNetworkingStepDef() {
        Given("^service is up and running$", () -> {
            setHeadersWithContentType();
            setEndpointPath(BaseURL);
            getMethodCall();
            responseForServiceCall = getResponse();
            assertThat(responseForServiceCall.statusCode(), is(equalTo(200)));

        });

        When("^I search for \"([^\"]*)\" of a post with a GET method$", (String id) -> {
            setHeadersWithContentType();
            setEndpointPath(MakeAPost + id);
            getMethodCall();
            responseForGetPostCall = getResponse();

        });

        Then("^I should get the correct \"([^\"]*)\", \"([^\"]*)\" and \"([^\"]*)\" with status code of (\\d+)$", (String id, String title, String body, Integer sCode) -> {
            assertThat(responseForGetPostCall.body().jsonPath().get("id").toString(), is(id));
            assertThat(responseForGetPostCall.body().jsonPath().get("title"), is(equalTo(title)));
            assertThat(responseForGetPostCall.body().jsonPath().get("body"), is(equalTo(body)));
            assertThat(responseForGetPostCall.statusCode(), is(equalTo(sCode)));
        });
        When("^i search for comment with \"([^\"]*)\" Get method$", (String id) -> {
            setHeadersWithContentType();
            setEndpointPath(MakeAComment + id);
            getMethodCall();
            responseForGetCommentCall = getResponse();

        });
        Then("^the correct \"([^\"]*)\", \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\" are returned with status code of (\\d+)$",
                (String id, String name, String email, String body, Integer sCode) -> {
                    assertThat(responseForGetCommentCall.body().jsonPath().get("id").toString(), is(id));
                    assertThat(responseForGetCommentCall.body().jsonPath().get("name"), is(equalTo(name)));
                    assertThat(responseForGetCommentCall.body().jsonPath().get("email"), is(equalTo(email)));
                    assertThat(responseForGetCommentCall.body().jsonPath().get("body"), is(equalTo(body)));
                    assertThat(responseForGetCommentCall.statusCode(), is(equalTo(sCode)));
                });
        When("^I create a new post with the following details \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\",$",
                (String uId, String title, String body) -> {
//                    setHeadersWithNoContentType();
                    setHeadersWithContentType();
                    requestBodyService = new RequestBodyService();
                    requestBody = loadTheJsonTemplate(MakeAPostPayloadPath);
                    requestBodyService.setRequestBodyForPostingNewPost(requestBody, uId, title, body);
                    setEndpointPath(MakeAPost);
                    postMethodCall();
                    responseForPostCall = getResponse();
                });
        Then("^i should get the correct \"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\" returned with status code of (\\d+)$",
                (String uId, String title, String body, Integer sCode) -> {
                    assertThat(responseForPostCall.statusCode(), is(sCode));
                    assertThat(responseForPostCall.body().jsonPath().get("userId").toString(), is(uId));
                    assertThat(responseForPostCall.body().jsonPath().get("title"), is(title));
                    assertThat(responseForPostCall.body().jsonPath().get("body"), is(body));
                });
        When("^I create a new comment with the following details \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\",$",
                (String postId, String name, String email, String body) -> {
                    setHeadersWithContentType();
                    requestBodyService = new RequestBodyService();
                    requestBody = loadTheJsonTemplate(MakeACommentPayloadPath);
                    requestBodyService.setRequestBodyForPostingNewComment(requestBody, postId, name, email, body);
                    setEndpointPath(MakeAComment);
                    postMethodCall();
                    responseForCommentCall = getResponse();
        });
        Then("^i should get the correct \"([^\"]*)\",\"([^\"]*)\",\"([^\"]*)\" and \"([^\"]*)\", returned with status code of (\\d+)$",
                (String postId, String name, String email, String body, Integer sCode) -> {
                    assertThat(responseForCommentCall.statusCode(), is(sCode));
                    assertThat(responseForCommentCall.body().jsonPath().get("postId").toString(), is(postId));
                    assertThat(responseForCommentCall.body().jsonPath().get("name"), is(name));
                    assertThat(responseForCommentCall.body().jsonPath().get("email"), is(email));
                    assertThat(responseForCommentCall.body().jsonPath().get("body"), is(body));
        });

    }
}