import pytest
import requests
import logging
from rich.console import Console
from rich.logging import RichHandler
from rich.panel import Panel
from rich.text import Text
import json


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


def test_get_person_success(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    log.info(f"Testing GET request for valid person ID: {valid_person_id}")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 200
    data = response.json()
    assert "id" in data and "name" in data
    assert isinstance(data["id"], int)
    assert isinstance(data["name"], str)
    log.info("Test passed successfully")

def test_get_person_not_found(invalid_person_id):
    url = f"{BASE_URL}/person/{invalid_person_id}"
    log.info(f"Testing GET request for invalid person ID: {invalid_person_id}")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 404
    log.info("Test passed successfully")

def test_get_person_invalid_id_format():
    url = f"{BASE_URL}/person/invalid_id"
    log.info("Testing GET request with invalid ID format")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [400, 404]
    log.info("Test passed successfully")

def test_get_person_negative_id():
    url = f"{BASE_URL}/person/-1"
    log.info("Testing GET request with negative ID")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [400, 404]
    log.info("Test passed successfully")


def test_update_person_success(valid_person_id, valid_update_data):
    url = f"{BASE_URL}/person/{valid_person_id}"
    log.info(f"Testing PUT request to update person with ID: {valid_person_id}")
    
    response = requests.put(url, json=valid_update_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=valid_update_data)
    
    assert response.status_code == 204
    log.info("Test passed successfully")

def test_update_person_not_found(invalid_person_id):
    url = f"{BASE_URL}/person/{invalid_person_id}"
    update_data = {
        "id": invalid_person_id,
        "name": "Non-existent Person"
    }
    log.info(f"Testing PUT request for non-existent person ID: {invalid_person_id}")
    
    response = requests.put(url, json=update_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=update_data)
    
    assert response.status_code == 404
    log.info("Test passed successfully")

def test_update_person_mismatched_ids(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    mismatched_data = {
        "id": valid_person_id + 1,
        "name": "Mismatched ID"
    }
    log.info("Testing PUT request with mismatched IDs")
    
    response = requests.put(url, json=mismatched_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=mismatched_data)
    
    assert response.status_code in [400, 422]
    log.info("Test passed successfully")

def test_update_person_invalid_data_types(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    invalid_data = {
        "id": "not an integer",
        "name": 12345
    }
    log.info("Testing PUT request with invalid data types")
    
    response = requests.put(url, json=invalid_data)
    log_request_response("PUT", url, response.status_code, response.text, request_data=invalid_data)
    
    assert response.status_code in [400, 422]
    log.info("Test passed successfully")


def test_delete_person_success(valid_person_id):
    url = f"{BASE_URL}/person/{valid_person_id}"
    log.info(f"Testing DELETE request for valid person ID: {valid_person_id}")
    
    response = requests.delete(url)
    log_request_response("DELETE", url, response.status_code, response.text)
    
    assert response.status_code == 200
    log.info("Test passed successfully")

def test_delete_person_not_found(invalid_person_id):
    url = f"{BASE_URL}/person/{invalid_person_id}"
    log.info(f"Testing DELETE request for non-existent person ID: {invalid_person_id}")
    
    response = requests.delete(url)
    log_request_response("DELETE", url, response.status_code, response.text)
    
    assert response.status_code == 409
    log.info("Test passed successfully")

def test_delete_person_invalid_id_format():
    url = f"{BASE_URL}/person/invalid_id"
    log.info("Testing DELETE request with invalid ID format")
    
    response = requests.delete(url)
    log_request_response("DELETE", url, response.status_code, response.text)
    
    assert response.status_code in [400, 409]
    log.info("Test passed successfully")

def test_delete_person_negative_id():
    url = f"{BASE_URL}/person/-1"
    log.info("Testing DELETE request with negative ID")
    
    response = requests.delete(url)
    log_request_response("DELETE", url, response.status_code, response.text)
    
    assert response.status_code in [400, 409]
    log.info("Test passed successfully")


def test_get_all_persons_default_params():
    url = f"{BASE_URL}/person"
    log.info("Testing GET request for all persons with default parameters")
    
    response = requests.get(url)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    if len(data) > 0:
        assert all("id" in item and "name" in item for item in data)
    log.info("Test passed successfully")

def test_get_all_persons_with_pagination():
    params = {
        "page": 0,
        "size": 5,
        "sort": "DESC"
    }
    url = f"{BASE_URL}/person"
    log.info("Testing GET request with pagination and sorting")
    
    response = requests.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    assert len(data) <= params["size"]
    log.info("Test passed successfully")

def test_get_all_persons_second_page():
    params = {
        "page": 1,
        "size": 5,
        "sort": "ASC"
    }
    url = f"{BASE_URL}/person"
    log.info("Testing GET request for second page")
    
    response = requests.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    log.info("Test passed successfully")

def test_get_all_persons_invalid_pagination():
    params = {
        "page": -1,
        "size": -5,
        "sort": "INVALID"
    }
    url = f"{BASE_URL}/person"
    log.info("Testing GET request with invalid pagination parameters")
    
    response = requests.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code in [400, 500]
    log.info("Test passed successfully")

def test_get_all_persons_large_page_size():
    params = {
        "page": 0,
        "size": 1000,
        "sort": "ASC"
    }
    url = f"{BASE_URL}/person"
    log.info("Testing GET request with large page size")
    
    response = requests.get(url, params=params)
    log_request_response("GET", url, response.status_code, response.text)
    
    assert response.status_code == 200
    data = response.json()
    assert isinstance(data, list)
    log.info("Test passed successfully")


def test_create_person_success(new_person_data):
    url = f"{BASE_URL}/person"
    log.info("Testing POST request to create new person")
    
    response = requests.post(url, json=new_person_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=new_person_data)
    
    assert response.status_code == 201
    assert isinstance(response.json(), int)
    log.info("Test passed successfully")
    return response.json()

def test_create_person_invalid_data():
    url = f"{BASE_URL}/person"
    invalid_data = {
        "id": "not an integer",
        "name": 12345
    }
    log.info("Testing POST request with invalid data")
    
    response = requests.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    
    assert response.status_code == 500
    log.info("Test passed successfully")

def test_create_person_missing_required_fields():
    url = f"{BASE_URL}/person"
    invalid_data = {}
    log.info("Testing POST request with missing required fields")
    
    response = requests.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    
    assert response.status_code == 500
    log.info("Test passed successfully")

def test_create_person_empty_name():
    url = f"{BASE_URL}/person"
    invalid_data = {
        "name": ""
    }
    log.info("Testing POST request with empty name")
    
    response = requests.post(url, json=invalid_data)
    log_request_response("POST", url, response.status_code, response.text, request_data=invalid_data)
    
    assert response.status_code == 500
    log.info("Test passed successfully")

def test_create_person_extra_fields():
    url = f"{BASE_URL}/person"
    data_with_extra = {
        "name": "John Doe",
        "extra_field": "extra value"
    }
    log.info("Testing POST request with extra fields")
    
    response = requests.post(url, json=data_with_extra)
    log_request_response("POST", url, response.status_code, response.text, request_data=data_with_extra)
    
    assert response.status_code in [201, 500] 
    log.info("Test passed successfully")

if __name__ == "__main__":
    console.print(Panel(
        Text("Running Extended Person API Tests", style="bold magenta"),
        expand=True
    ))
    pytest.main([__file__, "-v"])
