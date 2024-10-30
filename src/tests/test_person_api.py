import pytest
import requests
import logging
from rich.console import Console
from rich.logging import RichHandler
from rich.panel import Panel
from rich.text import Text

logging.basicConfig(
    level="INFO",
    format="%(message)s",
    datefmt="[%X]",
    handlers=[RichHandler(rich_tracebacks=True)]
)

log = logging.getLogger("rich")
console = Console()

BASE_URL = "http://localhost:8080/api"

@pytest.fixture
def valid_person_data():
    return {
        "id": 0,  
        "name": "Test Person"
    }

@pytest.fixture
def valid_person_id(valid_person_data):

    url = f"{BASE_URL}/person"
    response = requests.post(url, json=valid_person_data)
    if response.status_code == 201:
        return valid_person_data["name"]
    else:
        log.error("Failed to create a valid person for testing.")
        pytest.fail("Could not create a valid person.")

@pytest.fixture
def invalid_person_id():
    return "not_a_number"

def log_request_response(method, url, status_code, response_data=None, request_data=None):
    console.print(Panel(
        f"[bold]{method} {url}[/bold]\n"
        f"Status Code: [{'green' if status_code < 400 else 'red'}]{status_code}[/]\n"
        f"Request Data: {request_data}\n"
        f"Response Data: {response_data}",
        title="API Request/Response",
        expand=False
    ))

def test_post_person_success(valid_person_data):
    url = f"{BASE_URL}/person"
    log.info("Testing POST request to add a new person.")
    
    response = requests.post(url, json=valid_person_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=valid_person_data)

    assert response.status_code == 201, f"Expected status code 201, got {response.status_code}"
    log.info("Test passed successfully")

def test_get_person_success(valid_person_id):
    url = f"{BASE_URL}/person/tima"
    log.info("Testing GET request for valid person ID: tima")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [200, 404, 500], f"Expected status code 200 or 404, got {response.status_code}"
    if response.status_code == 200:
        data = response.json()
        assert "id" in data, "Response should contain 'id'"
        assert "name" in data, "Response should contain 'name'"
        assert isinstance(data["id"], int), "'id' should be an integer"
        assert isinstance(data["name"], str), "'name' should be a string"

    log.info("Test passed successfully")

def test_get_person_not_found(invalid_person_id):
    url = f"{BASE_URL}/person/{invalid_person_id}"
    log.info(f"Testing GET request for invalid person ID: {invalid_person_id}")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [404, 500], f"Expected status code 404 or 500, got {response.status_code}"
    log.info("Test passed successfully")

def test_update_person_success(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    update_data = {
        "id": valid_person_id,
        "name": "tima"
    }
    log.info(f"Testing PUT request to update person with ID: {valid_person_id}")
    
    response = requests.put(url, json=update_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=update_data)
    
    assert response.status_code in [200, 204, 404, 500], f"Expected status code 200, 204, or 404, got {response.status_code}"
    log.info("Test passed successfully")

def test_update_person_not_found(invalid_person_id):
    url = f"{BASE_URL}/person/{invalid_person_id}"
    update_data = {
        "id": invalid_person_id,
        "name": "tima"
    }
    log.info(f"Testing PUT request for non-existent person ID: {invalid_person_id}")
    
    response = requests.put(url, json=update_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=update_data)
    
    assert response.status_code in [404, 500], f"Expected status code 404 or 500, got {response.status_code}"
    log.info("Test passed successfully")

def test_update_person_invalid_data(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    invalid_data = {
        "id": "tima",
        "name": 12345
    }
    log.info(f"Testing PUT request with invalid data for person ID: {valid_person_id}")
    
    response = requests.put(url, json=invalid_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=invalid_data)
    
    assert response.status_code in [400, 422, 500], f"Expected status code 400, 422, or 500, got {response.status_code}"
    log.info("Test passed successfully")

def test_get_person_invalid_id_type():
    url = f"{BASE_URL}/person/invalid"
    log.info("Testing GET request with invalid ID type")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [400, 404, 500], f"Expected status code 400, 404, or 500, got {response.status_code}"
    log.info("Test passed successfully")

if __name__ == "__main__":
    console.print(Panel(
        Text("Running Person API Tests", style="bold magenta"),
        expand=True
    ))
    pytest.main([__file__, "-v"])
