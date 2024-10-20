This framework tests multiple authentication flows and functionalities of spotify api 
such as reading playlist, getting artists and songs data, uploading files, creating playlist and adding songs etc.


Tools used: Rest assured, Playwright, TestNG, Jackson, Extent spark reporter and other java utils.


1. First part is gaining the code by using Playwright(java) in headless mode, 
by hitting the url with specific user data and required scopes
provided after redirection to designated url the code has been parsed.

2. Obtaing access token by sending apikey, apisecret, and code.

3. Complete test flow is straight forward with positive scenarios only with some dependecies 
between methods  hard assertions are used only.
All utility actions are dynamic such as creating complex payloads and transfering 
data from test to test.

4. Extent reports are created upon every test run as html file.