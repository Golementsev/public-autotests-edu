import pytest
from client import vet_api
import allure

@allure.feature('Post person, positive script')
@allure.story('POST /person')
def test_post_get_person(vet_api):
    resp = vet_api.post('person', json={'id': 1, 'name': 'artem'})
    with allure.step("Request sent."):
        assert resp.status_code == 201, f"Wrong code, 201 expected"

    with allure.step("Status code successful, get person id"):
        post_resp_json = resp.json()
        print(post_resp_json)
        resp = vet_api.get(f'person/{post_resp_json}')
        get_resp_json = resp.json()
        assert post_resp_json == get_resp_json['id']

    with allure.step("Testing some positive id's"):
        positive_ids = [462539845238, 90, 52, 69, 42, 1488, '1']
        for id in positive_ids:
            with allure.step(f"Testing {id} id"):
                resp = vet_api.post('person', json={'id': id, 'name': 'artem1'})
                assert resp.status_code == 201, f"Wrong code, 201 expected"


@allure.feature('Post person, negative scripts')
@allure.story("GET /person")
def test_post_negative(vet_api):
    with allure.step("Test non-validating id's"):
        ids = [-1, 'aa', '-q0']
        for id in ids:
            with allure.step(f"Testing {id} id"):
                resp = vet_api.post('person', json={'id': id, 'name': 'artem1'})
                assert resp.status_code == 400 or resp.status_code == 500, f"Wrong code, 400 or 500 expected"
    with allure.step('Test SQL injections'):
        names = ['123; DELETE * FROM users;']
        for name in names:
            with allure.step(f"Testing {id} id"):
                resp = vet_api.post('person', json={'id': '1', 'name': name})
                assert resp.status_code == 400 or resp.status_code == 500, f"Wrong code, 400 or 500 expected"