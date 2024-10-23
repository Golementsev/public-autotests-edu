import pytest
import requests
import logging
from rich.console import Console
from rich.logging import RichHandler
from rich.panel import Panel
from rich.text import Text
import json
from client import vet_api
import allure

# Настройка логирования
logging.basicConfig(
    level="INFO",
    format="%(message)s",
    datefmt="[%X]",
    handlers=[RichHandler(rich_tracebacks=True)]
)

log = logging.getLogger("rich")
console = Console()

BASE_URL = "http://localhost:8080/api"

# Фикстуры
@pytest.fixture
def valid_person_id():
    return 1

@pytest.fixture
def invalid_person_id():
    return 99999

@pytest.fixture
def new_person_data():
    return {
        "name": "John Doe New"
    }

@pytest.fixture
def valid_update_data(valid_person_id):
    return {
        "id": valid_person_id,
        "name": "John Doe Updated"
    }

def log_request_response(method, url, status_code, response_data=None, request_data=None):
    console.print(Panel(
        f"[bold]{method} {url}[/bold]\n"
        f"Status Code: [{'green' if status_code < 400 else 'red'}]{status_code}[/]\n"
        f"Request Data: {request_data}\n"
        f"Response Data: {response_data}",
        title="API Request/Response",
        expand=False
    ))

# GET /person/{id} tests
@allure.feature("Valid GET request")
def test_get_person_success(vet_api, valid_person_id):
    log.info(f"Testing GET request for valid person ID: {valid_person_id}")
    response = vet_api.get("person/{valid_person_id}")
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Request sent"):
        assert response.status_code == 200
        data = response.json()
        assert "id" in data and "name" in data
        assert isinstance(data["id"], int)
        assert isinstance(data["name"], str)
        log.info("Test passed successfully")

@allure.feature("Not existed data")
def test_get_person_not_found(vet_api, invalid_person_id):
    response = vet_api.get(f"person/{invalid_person_id}")
    log.info(f"Testing GET request for invalid person ID: {invalid_person_id}")
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Checking response"):
        assert response.status_code == 404
        log.info("Test passed successfully")

@allure.feature("GET invalid ID")
def test_get_person_invalid_id_format(vet_api):
    response = vet_api.get(f"person/invalid_id")
    log.info("Testing GET request with invalid ID format")
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Checking response"):
        assert response.status_code in [400, 404]
        log.info("Test passed successfully")

@allure.feature("GET negative ID")
def test_get_person_negative_id(vet_api):
    response = vet_api.get(f"person/-1")
    log.info("Testing GET request with negative ID")
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Checking status code"):
        assert response.status_code in [400, 404]
        log.info("Test passed successfully")

# PUT /person/{id} tests
@allure.feature("PUT positive script")
def test_update_person_success(vet_api, valid_person_id, valid_update_data):
    response = vet_api.put(f"person/{valid_person_id}", json=valid_update_data)
    log.info(f"Testing PUT request to update person with ID: {valid_person_id}")
    log_request_response("PUT", url, response.status_code, response.text, request_data=valid_update_data)
    with allure.step("Checking status code"):
        assert response.status_code == 204
        log.info("Test passed successfully")

@allure.feature("PUT with unexisted data")
def test_update_person_not_found(vet_api, invalid_person_id):
    update_data = {
        "id": invalid_person_id,
        "name": "Non-existent Person"
    }
    log.info(f"Testing PUT request for non-existent person ID: {invalid_person_id}")
    response = vet_api.put(f"person/{invalid_person_id}", json=update_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=update_data)
    with allure.step("Check status codes"):
        assert response.status_code == 404
        log.info("Test passed successfully")

@allure.feature("PUT with mismatched ID")
def test_update_person_mismatched_ids(vet_api, valid_person_id):
    mismatched_data = {
        "id": valid_person_id + 1,
        "name": "Mismatched ID"
    }
    log.info("Testing PUT request with mismatched IDs")
    response = vet_api.put(f"person/{valid_person_id}", json=mismatched_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=mismatched_data)
    with allure.step("Checking status code"):
        assert response.status_code in [400, 422]
        log.info("Test passed successfully")

@allure.feature("PUT with invalid data types")
def test_update_person_invalid_data_types(vet_api, valid_person_id):
    invalid_data = {
        "id": "not an integer",
        "name": 12345
    }
    log.info("Testing PUT request with invalid data types")
    response = vet_api.put(f"person/{valid_person_id}", json=invalid_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=invalid_data)
    with allure.step("Checking status codes"):
        assert response.status_code in [400, 422]
        log.info("Test passed successfully")

# DELETE /person/{id} tests
@allure.feature("Delete valid person")
def test_delete_person_success(vet_api, valid_person_id):
    response = vet_api.delete("person/{valid_person_id}")
    log.info(f"Testing DELETE request for valid person ID: {valid_person_id}")
    log_request_response("DELETE", url, response.status_code, response.text)
    with allure.step("Checking status codes"):
        assert response.status_code == 200
        log.info("Test passed successfully")

@allure.feature("Delete unexsting person")
def test_delete_person_not_found(vet_api,invalid_person_id):
    response = vet_api.delete(f"person/{invalid_person_id}")
    log.info(f"Testing DELETE request for non-existent person ID: {invalid_person_id}")
    log_request_response("DELETE", url, response.status_code, response.text)
    with allure.step("Checking status code"):
        assert response.status_code == 409
        log.info("Test passed successfully")

@allure.feature("Delete person with invalid ID")
def test_delete_person_invalid_id_format(vet_api):
    log.info("Testing DELETE request with invalid ID format")
    
    response = vet_api.delete(f"person/invalid_id")
    log_request_response("DELETE", url, response.status_code, response.text)
    with allure.step("Checking status code"):
        assert response.status_code in [400, 409]
        log.info("Test passed successfully")

@allure.feature("Delete person with negative ID")
def test_delete_person_negative_id(vet_api):
    url = f"person/-1"
    log.info("Testing DELETE request with negative ID")
    
    response = vet_api.delete(url)
    log_request_response("DELETE", url, response.status_code, response.text)
    with allure.step("Checking status codes"):
        assert response.status_code in [400, 409]
        log.info("Test passed successfully")

# GET /person (all persons) tests
@allure.feature("Get all person")
def test_get_all_persons_default_params(vet_api):
    url = f"person"
    log.info("Testing GET request for all persons with default parameters")
    
    response = vet_api.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Cheking status code"):
        assert response.status_code == 200
        data = response.json()
    with allure.step("Check data validity"):
        assert isinstance(data, list)
        if len(data) > 0:
            assert all("id" in item and "name" in item for item in data)
        log.info("Test passed successfully")

@allure.feature("Get all person with pagination")
def test_get_all_persons_with_pagination(vet_api):
    params = {
        "page": 0,
        "size": 5,
        "sort": "DESC"
    }
    url = f"person"
    log.info("Testing GET request with pagination and sorting")
    
    response = vet_api.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Check status codes"):
        assert response.status_code == 200
    data = response.json()
    with allure.step("Check data validity"):
        assert isinstance(data, list)
        assert len(data) <= params["size"]
        log.info("Test passed successfully")

@allure.feature('Get all person (second page)')
def test_get_all_persons_second_page(vet_api):
    params = {
        "page": 1,
        "size": 5,
        "sort": "ASC"
    }
    url = f"person"
    log.info("Testing GET request for second page")
    
    response = vet_api.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Check status code"):
        assert response.status_code == 200
    with allure.step("Check data validity"):
        data = response.json()
        assert isinstance(data, list)
        log.info("Test passed successfully")

@allure.feature("Get all person (invalid pagination)")
def test_get_all_persons_invalid_pagination(vet_api):
    params = {
        "page": -1,
        "size": -5,
        "sort": "INVALID"
    }
    url = f"person"
    log.info("Testing GET request with invalid pagination parameters")
    
    response = vet_api.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Check status codes"):
        assert response.status_code in [400, 500]
        log.info("Test passed successfully")

@allure.feature("Get all person (large page size)")
def test_get_all_persons_large_page_size(vet_api):
    params = {
        "page": 0,
        "size": 1000,
        "sort": "ASC"
    }
    url = f"person"
    log.info("Testing GET request with large page size")
    
    response = vet_api.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    with allure.step("Check status code"):
        assert response.status_code == 200
    with allure.step("Check data validity"):
        data = response.json()
        assert isinstance(data, list)
        log.info("Test passed successfully")

# POST /person tests
@allure.feature("Create person, positive script")
def test_create_person_success(vet_api, new_person_data):
    url = f"person"
    log.info("Testing POST request to create new person")
    
    response = vet_api.post(url, json=new_person_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=new_person_data)
    with allure.step("Check status code"):
        assert response.status_code == 201
    with allure.step("Check data validity"):
        assert isinstance(response.json(), int)
        log.info("Test passed successfully")
        return response.json()

@allure.feature("Create person with invalid data")
def test_create_person_invalid_data(vet_api):
    url = f"person"
    invalid_data = {
        "id": "not an integer",
        "name": 12345
    }
    log.info("Testing POST request with invalid data")
    
    response = vet_api.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    with allure.step("Check code status"):
        assert response.status_code == 500
        log.info("Test passed successfully")

@allure.feature("Create person, missing fields")
def test_create_person_missing_required_fields(vet_api):
    url = f"person"
    invalid_data = {}
    log.info("Testing POST request with missing required fields")
    
    response = vet_api.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    with allure.step("Check status code"):
        assert response.status_code == 500
        log.info("Test passed successfully")
@allure.feature("Create person, empty name")
def test_create_person_empty_name(vet_api):
    url = f"person"
    invalid_data = {
        "name": ""
    }
    log.info("Testing POST request with empty name")
    
    response = vet_api.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    with allure.step("Check status code"):
        assert response.status_code == 500
        log.info("Test passed successfully")

@allure.feature("Create person with extra fields")
def test_create_person_extra_fields(vet_api):
    url = f"person"
    data_with_extra = {
        "name": "John Doe",
        "extra_field": "extra value"
    }
    log.info("Testing POST request with extra fields")
    
    response = vet_api.post(url, json=data_with_extra)
    log_request_response("POST", url, response.status_code, response.text, request_data=data_with_extra)
    with allure.step("Check status code"):
        assert response.status_code in [201, 500]  # Зависит от того, как API обрабатывает лишние поля
        log.info("Test passed successfully")

if __name__ == "__main__":
    console.print(Panel(
        Text("Running Extended Person API Tests", style="bold magenta"),
        expand=True
    ))
    pytest.main([__file__, "-v"])
