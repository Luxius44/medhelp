package com.example.medicine

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import com.example.data.MedicineData
import com.example.data.ReminderData
import com.example.utils.Util
import com.google.gson.Gson
import org.junit.Assert
import org.junit.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import java.util.Calendar


class UtilTest {

    val context = mock(Context::class.java)

    val mockAlarmManager: AlarmManager = mock(AlarmManager::class.java)

    val mockNotificationManager: NotificationManager = mock(NotificationManager::class.java)

    /*@Test
    fun testAddReminder() {
        val alarmManager =   Robolectric.application.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val shadowAlarmManager = Robolectric.shadowOf(alarmManager)

        val mockPendingIntent: PendingIntent = mock(PendingIntent::class.java)
        val calendar = Calendar.getInstance()
        `when`(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(mockAlarmManager)
        `when`(context.getSystemService(NotificationManager::class.java)).thenReturn(mockNotificationManager)
        `when`(mockAlarmManager.setExact(
            1,
            calendar.timeInMillis,
            mockPendingIntent)).thenAnswer {null}

        `when`(context.getSystemService(Context.ALARM_SERVICE)).thenReturn(AlarmManager())

        val reminderData = ReminderData()
        reminderData.requestCode = 1
        reminderData.hourReminder = "10:00"
        reminderData.listDays = mutableListOf("L","V")

        Util.addReminder(context, reminderData)

        verify(mockAlarmManager).setExact(anyInt(), anyLong(), mockPendingIntent)
    }*/


    @Test
    fun testListElementInCache() {
        // Configuration du comportement du mock pour la méthode getSharedPreferences()
        val sharedPreferences = mock(SharedPreferences::class.java)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)

        // Appeler la fonction à tester pour les rappels (ReminderData)
        val reminders = Util.listElementInCache<ReminderData>(context)
        Assert.assertEquals(0, reminders.size)

        // Appeler la fonction à tester pour les médicaments (MedicineData)
        val medicines = Util.listElementInCache<MedicineData>(context)
        Assert.assertEquals(0, medicines.size)

        // Appeler la fonction à tester pour autre chose que des rappels et médicaments
        val listNull = Util.listElementInCache<String>(context)
        Assert.assertEquals(0, listNull.size)
    }

    @Test
    fun testListElementSaveMedicine() {
        // Mock SharedPreferences
        val sharedPreferencesEditor = mock(SharedPreferences.Editor::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)

        val medicine = MedicineData("doliprane","500 mg",5,8,"Jour(s)",9,"Jour(s)",2,"Comprimé","14/02/2025",true)

        val reminderList = mutableListOf(medicine)

        Util.listElementSave(context, reminderList)

        // Vérifie que c’est les bonnes données qui sont stockées dans le mock sharedPreferences
        val gson = Gson()
        val expectedJson = gson.toJson(reminderList)
        val expectedTypeElement = "ordonnances"
        verify(sharedPreferencesEditor).putString(expectedTypeElement, expectedJson)
        verify(sharedPreferencesEditor).apply()
    }

    @Test
    fun testListElementSaveNothing() {
        // Mock SharedPreferences
        val sharedPreferencesEditor = mock(SharedPreferences.Editor::class.java)
        val sharedPreferences = mock(SharedPreferences::class.java)
        `when`(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)
        `when`(context.getSharedPreferences(anyString(), anyInt())).thenReturn(sharedPreferences)

        val listString = mutableListOf("Test","test2")

        Util.listElementSave(context, listString)
    }

    @Test
    fun testVerifyDayForReminder(){
        `when`(context.getString(R.string.monday_mini)).thenReturn("L")
        `when`(context.getString(R.string.tuesday_mini)).thenReturn("Ma")
        val reminderTest = ReminderData(1,null,null,null,null,null,null, mutableListOf(context.getString(R.string.tuesday_mini)))
        val calendarTest = Calendar.getInstance()
        calendarTest.set(2024,2,25)
        val calendarResult = Util.verifyDayForReminder(context, reminderTest, calendarTest)
        Assert.assertEquals(3,calendarResult.get(Calendar.DAY_OF_WEEK))
        Assert.assertEquals(26, calendarResult.get(Calendar.DAY_OF_MONTH))
    }

    /*@Test
    fun testShowDatePickerDialog() {
        MockitoAnnotations.initMocks(this)
        val myClassUnderTest = Util
        val dateEditText = EditText(context)

        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)
        val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val selectedDate = Calendar.getInstance()

        // Mock DatePickerDialog
        val datePickerDialog = Mockito.mock(DatePickerDialog::class.java)

        val captor = ArgumentCaptor.forClass(DatePickerDialog.OnDateSetListener::class.java)

        // Capture the listener passed to the DatePickerDialog
        verify(datePickerDialog).setOnDateSetListener(captor.capture())

        // Trigger the listener manually
        captor.value.onDateSet(Mockito.mock(DatePicker::class.java), currentYear, currentMonth, currentDay)

        // Check if EditText is updated with the selected date
        verify(dateEditText).setText(dateFormat.format(selectedDate.time))
    }

    @Test
    fun testShowDatePickerDialog2() {
        MockitoAnnotations.initMocks(this)
        val yourClassUnderTest = Util
        val dateEditText = EditText(context)

        val mockDatePickerDialog = mock(DatePickerDialog::class.java)

        // Mocking the DatePickerDialog behavior
        `when`(mockDatePickerDialog.setOnDateSetListener(any())).thenAnswer {
            val listener = it.arguments[0] as DatePickerDialog.OnDateSetListener
            val calendar = Calendar.getInstance()
            val year = 2024
            val month = Calendar.MARCH // Month is zero-based
            val dayOfMonth = 31
            listener.onDateSet(null, year, month, dayOfMonth)
        }

        doNothing().`when`(mockDatePickerDialog).show()

        // Call the method you want to test
        yourClassUnderTest.showDatePickerDialog(context, dateEditText)

        // Verify that the EditText text is set with the correct date
        val expectedDate = "31/03/2024"
        verify(dateEditText).setText(expectedDate)
    }*/
}


