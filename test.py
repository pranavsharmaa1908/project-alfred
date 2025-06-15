from gcsa.google_calendar import GoogleCalendar

gc = GoogleCalendar(credentials_path='.credentials/client_secret_1000504010656-qehmbhfpdfq3gc0jbagrprgcvn426q6t.apps.googleusercontent.com.json')

for event in gc:
    print(event)

for event in gc.get_events(query="Pranav"):
    print(event)
