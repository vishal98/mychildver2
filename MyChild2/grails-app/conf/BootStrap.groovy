
import java.util.Date;




import ghumover2.*;
import grails.converters.JSON
import groovy.sql.Sql



class BootStrap {

	private static final String ROLE_TEACHER = 'ROLE_TEACHER'
	private static final String ROLE_PARENT = 'ROLE_PARENT'

	
	def dataSource
	def init = { servletContext ->

		

		// BOOTSTRAPING DATES
		def createQuery = "CREATE TABLE IF NOT EXISTS ints ( i tinyint unique );"
		def insertQuery = "INSERT INTO ints (i) VALUES (0),(1),(2),(3),(4),(5),(6),(7),(8),(9)   ON DUPLICATE KEY UPDATE i = VALUES(i);"
		def insertCalenderDates = """\

										INSERT INTO calendar_date (calendar_date)
										SELECT DATE('2010-01-01') + INTERVAL a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i DAY
										FROM ints a JOIN ints b JOIN ints c JOIN ints d JOIN ints e
										WHERE (a.i*10000 + b.i*1000 + c.i*100 + d.i*10 + e.i) <= 11322
										ORDER BY 1;
                                    """
		def updateCalenderDates = """\
															UPDATE calendar_date
															SET is_weekday = CASE WHEN dayofweek(calendar_date) IN (1,7) THEN 0 ELSE 1 END,
															is_holiday = 0,
															is_payday = 0,
															year = YEAR(calendar_date),
															quarter = quarter(calendar_date),
															month = MONTH(calendar_date),
															day_of_month = dayofmonth(calendar_date),
															day_of_week = dayofweek(calendar_date),
															month_name = monthname(calendar_date),
															day_name = dayname(calendar_date),
															week_of_year = week(calendar_date),
															holiday_description = '';
"""

		def sql = new Sql(dataSource)

		sql.executeUpdate(createQuery)
		sql.executeUpdate(insertQuery)

		sql.executeUpdate(insertCalenderDates)
		sql.executeUpdate(updateCalenderDates)

		//ADDED AUGUST 15 AS HOLIDAY
				CalendarDate.executeUpdate("update CalendarDate c set c.holiday_description='Independance Day' , is_holiday = true " +
				"where c.month=8 and c.day_of_month = 15")


		// END OF BOOTSTRAPING DATES



		Role roleTeacher;
		Role roleParent;
		Teacher teacher;
		Guardian parent;


		roleTeacher = new Role(authority: ROLE_TEACHER)
		roleTeacher.save()

		roleParent = new Role(authority: ROLE_PARENT)
		roleParent.save()




		teacher = new Teacher(username: 'test_teacher', password: "123" , teacherId:"100" , teacherName:"John" , teacherPhoto:"100.jpg",teacherEmailId:"vis@123",phoneNo:"9634444")
		teacher.save()
		new UserRole(user:teacher , role:roleTeacher).save()





		// ADD 4 SAMPLE GRADES 5A,5B,10A AND 10B

		new Grade(name: 5 , section:"A").save(flush:true)
		new Grade(name:5 , section:"B").save(flush:true)
		new Grade(name:10 , section:"A").save(flush:true)
		new Grade(name:10 , section:"B").save(flush:true)
		new Grade(name:6 , section:"A").save(flush:true)
		new Grade(name:6 , section:"B").save(flush:true)
		new Grade(name:7 , section:"A").save(flush:true)
		new Grade(name:7 , section:"B").save(flush:true)

		// Add 3 teacher entries

		new Teacher(username: 'mathew', password: "123" ,teacherId:101 , teacherName:"Mathew" , teacherPhoto:"100.jpg",teacherEmailId:"vis@123",phoneNo:"9634444").save(flush:true)
		new Teacher(username: 'sibi', password: "123" ,teacherId:102 , teacherName:"Sibi" , teacherPhoto:"101.jpg",teacherEmailId:"vis@123",phoneNo:"9634444").save(flush:true)
		new Teacher(username: 'satheesh', password: "123" ,teacherId:103 , teacherName:"Satheesh" , teacherPhoto:"102.jpg",teacherEmailId:"vis@123",phoneNo:"9634444").save(flush:true)




		// Add 2 student entries and a parent entry ,  assing 2 students to that parent

		def cl5A = Grade.get(1)
		def cl5B = Grade.get(2)
		def cl6A = Grade.get(5)
		def cl6B = Grade.get(6)
		def cl7A = Grade.get(7)
		def cl7B = Grade.get(8)
		def cl10A = Grade.get(3)
		def cl10B = Grade.get(4)


		def father , mother , local_guardian , s1 , s2 , s3
	   // FIRST STUDENT DETAILS
		s1 =  new Student(grade:cl5A  , registerNumber: "ST100" ,studentName: "Rohith" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Father" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()  ).save()
		s1.setAsFather( new Guardian(name: "Ravi" , username: "ravi@test.com" , password: "123" , educational_qualification: "MBA" , designation: "Manager" , profession: "Private Employee" , emailId: "father@user.com" , officeNumber: "04868699000" , mobileNumber: "98470000" ).save() )
		s1.setAsMother( new Guardian(name:"Raani" , username: "raani@test.com" , password: "123" , educational_qualification: "Bcom" , designation: "College Professor" , profession: "Lecturer" , emailId: "mother@user.com" ,officeNumber: "0489898989" , mobileNumber: "94466797979"  ).save() )

		father = Guardian.findByUsername("ravi@test.com")
		mother = Guardian.findByUsername("raani@test.com")

		s2 =  new Student(grade: cl5A , registerNumber: "ST101" ,studentName: "Renjith" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Father" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save() ).save()
		s2.setAsFather( father )
		s2.setAsMother( mother )

		s3 =  new Student(grade: cl6A ,  registerNumber: "ST102" ,studentName: "Rohan" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Father"  , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()).save()
		s3.setAsFather( father )
		s3.setAsMother( mother )

		new UserRole(user:father , role:roleParent).save(flush: true)
		new UserRole(user:mother , role:roleParent).save(flush: true)



	   // SECOND STUDENT DETAILS

		s1 =  new Student( grade:cl5A  , registerNumber: "ST103" ,studentName: "Midhun" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Local Guardian" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()  ).save()
		s1.setAsFather( new Guardian(name: "Mahadev" , username: "mahadev@test.com" , password: "123" , educational_qualification: "MBA" , designation: "Manager" , profession: "Private Employee" , emailId: "father@user.com" , officeNumber: "04868699000" , mobileNumber: "98470000" ).save() )
		s1.setAsMother( new Guardian(name:"Malini" , username: "malini@test.com" , password: "123" , educational_qualification: "Bcom" , designation: "College Professor" , profession: "Lecturer" , emailId: "mother@user.com" ,officeNumber: "0489898989" , mobileNumber: "94466797979"  ).save() )
		s1.setAsLocalGuardian((new Guardian(name:"Manish" , username: "manish@test.com" , password: "123" , educational_qualification: "MCA" , designation: "Software Engineer" , profession: "IT Professional" , emailId: "local_guard@test.com" ,officeNumber: "0489898989" , mobileNumber: "94466797979" )).save())
		father = Guardian.findByUsername("mahadev@test.com")
		mother = Guardian.findByUsername("malini@test.com")
		local_guardian = Guardian.findByUsername("manish@test.com")


		s2 =  new Student(grade: cl5A , registerNumber: "ST104" ,studentName: "Manoj" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Local Guardian" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()).save()
		s2.setAsFather( father )
		s2.setAsMother( mother )
		s2.setAsLocalGuardian( local_guardian )

		s3 =  new Student(grade: cl6A ,  registerNumber: "ST105" ,studentName: "Mohith" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Local Guardian", present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save() ).save()
		s3.setAsFather( father )
		s3.setAsMother( mother )
		s3.setAsLocalGuardian( local_guardian )

		new UserRole(user:father , role:roleParent).save(flush: true)
		new UserRole(user:mother , role:roleParent).save(flush: true)



		// third group STUDENT DETAILS

		s1 =  new Student( grade:cl5A  , registerNumber: "ST106" ,studentName: "Neha" , gender: "Female" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Mother" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()  ).save()
		s1.setAsFather( new Guardian(name: "Nagesh" , username: "nagesh@test.com" , password: "123" , educational_qualification: "MBA" , designation: "Manager" , profession: "Private Employee" , emailId: "father@user.com" , officeNumber: "04868699000" , mobileNumber: "98470000" ).save() )
		s1.setAsMother( new Guardian(name:"Nanditha" , username: "nanditha@test.com" , password: "123" , educational_qualification: "Bcom" , designation: "College Professor" , profession: "Lecturer" , emailId: "mother@user.com" ,officeNumber: "0489898989" , mobileNumber: "94466797979"  ).save() )

		father = Guardian.findByUsername("nagesh@test.com")
		mother = Guardian.findByUsername("nanditha@test.com")

		s2 =  new Student(grade: cl5A , registerNumber: "ST107" ,studentName: "Nivas" , gender: "Male" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Mother" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()).save()
		s2.setAsFather( father )
		s2.setAsMother( mother )

		s3 =  new Student( grade: cl6A ,  registerNumber: "ST108" ,studentName: "Nikhitha" , gender: "Female" , dob:"12-12-2000" , studentPhoto: "photo.jpg", no_of_siblings: 2 , present_guardian: "Mother" , present_address: new Address(address: "Sample Address" , landmark: "Cochin" , place: "Kerala" ).save()).save()
		s3.setAsFather( father )
		s3.setAsMother( mother )

		new UserRole(user:father , role:roleParent).save(flush: true)
		new UserRole(user:mother , role:roleParent).save(flush: true)





		def mathew = Teacher.findByTeacherId(100)
		def sibi = Teacher.findByTeacherId(101)
		def sathees = Teacher.findByTeacherId(102)

		new UserRole(user:mathew , role:roleTeacher).save(flush:true)
		new UserRole(user:sibi , role:roleTeacher).save(flush:true)
		new UserRole(user:sathees , role:roleTeacher).save(flush:true)




		cl5A.addToTeachers(mathew)

		cl5A.addToTeachers(sibi)
		cl5A.classTeacherId = mathew.id
		cl5A.classTeacherName = mathew.teacherName

		cl5A.save(flush:true)




		// Add some subjects and assing them to grades
  
		new Subject(subjectName:"English").save(flush:true)
		new Subject(subjectName:"Hindi").save(flush:true)
		new Subject(subjectName:"Physics").save(flush:true)
		new Subject(subjectName:"Chemistry").save(flush:true)
		new Subject(subjectName: "Mathematics" ).save(flush: true)
		new Subject(subjectName: "ComputerScience").save(flush: true)
		new Subject(subjectName: "History").save(flush: true)


		def english = Subject.get(1)
		def hindi = Subject.get(2)
		def physics = Subject.get(3)
		def chemistry = Subject.get(4)
		def maths = Subject.get(5)
		def computerScience = Subject.get(6)
		def history = Subject.get(7)
		
		mathew.addToSubject(english)
		.addToSubject(hindi).save(flush: true)
		
		
		cl5A.addToSubject(english)
		.addToSubject(physics)
		.addToSubject(chemistry)
		.addToSubject(computerScience).save(flush: true)
		cl5B.addToSubject(english)
		.addToSubject(physics)
		.addToSubject(chemistry)
		.addToSubject(computerScience).save(flush: true)
		cl7A.addToSubject(english)
		.addToSubject(physics)
		.addToSubject(chemistry)
		.addToSubject(computerScience).save(flush: true)
		cl10A.addToSubject(english)
		.addToSubject(physics)
		.addToSubject(chemistry)
		.addToSubject(computerScience).save(flush: true)
		cl10B.addToSubject(english)
		.addToSubject(physics)
		.addToSubject(chemistry)
		.addToSubject(computerScience).save(flush: true)


		//new GradeSubject(grade:5 ,subject:english).save(flush:true)
		//new GradeSubject(grade:5 ,subject:maths).save(flush:true)
		//new GradeSubject(grade:5 ,subject:hindi).save(flush:true)
		//new GradeSubject(grade:5 ,subject:history).save(flush:true)

		//new GradeSubject(grade:6 , subject:english).save(flush:true)
		//new GradeSubject(grade:6 , subject:hindi).save(flush:true)
		//new GradeSubject(grade:6 , subject:computerScience).save(flush:true)
		//new GradeSubject(grade:6 , subject:history).save(flush:true)

		//new GradeSubject(grade:7 , subject:english).save(flush:true)
		//new GradeSubject(grade:7 , subject:hindi).save(flush:true)
		//new GradeSubject(grade:7 , subject:computerScience).save(flush:true)
		//new GradeSubject(grade:7 , subject:history).save(flush:true)



		//new GradeSubject(grade:10 ,subject:physics).save(flush:true)
		//new GradeSubject(grade:10 , subject:chemistry).save(flush:true);
		//new GradeSubject(grade:10 , subject: history ).save(flush: true)
		//new GradeSubject(grade:10 , subject: english ).save(flush: true)
		//new GradeSubject(grade:10 , subject: computerScience ).save(flush: true)
		//new GradeSubject(grade:10 , subject: maths ).save(flush: true)


		new  Message ( value:"homeWork" ,type:"msg",code:"hw").save(flush:true)
		new  Message ( value:"notes" ,type:"msg",code:"nt").save(flush:true)
		new  Message ( value:"project" ,type:"msg",code:"proj").save(flush:true)



		//Add entries for home work





		//Homeworks for students

		   new Homework(grade: cl5A , subject: "english" , homework: "Project ", dueDate: "31-12-2016"  ,student: Student.findByStudentId(1) , message: "Scient Project for Student , 5 A ", gradeFlag: '0').save(flush: true)
			  // new Homework(homeworkId: 101 ,grade: cl5B , subject: "english" , homework: "English homework" ,  dueDate: "10-04-2015" ,section: "B", student: Student.findByStudentId(101) , message: "English Homework for Student ,  5 B  ", gradeFlag: '0').save(flush: true)
			  //  new Homework(homeworkId: 102 ,grade: cl6A , subject: "history" , homework: "History homework" ,dueDate: "9-04-2015" ,section: "A", student: Student.findByStudentId(102), message: "History Homework for Student , 6 A  ", gradeFlag: '0').save(flush: true)
			  // new Homework(homeworkId: 103 ,grade: cl6B , subject: "computerScience" , homework: "ComputerScience homework" , dueDate: "8-04-2015" ,section: "A", student: Student.findByStudentId(103) , message: "ComputerScience Homework for Student , 7 A ", gradeFlag: '0').save(flush: true)
			  // new Homework(homeworkId: 104 ,grade: cl10A , subject: "physics" ,homework: "Physics homework", dueDate: "7-04-2015" ,section: "A",student: Student.findByStudentId(104) , message: "Physics Homework for Student 10 A", gradeFlag: '0').save(flush: true)



		  //Homeworks for whole batch

				  new Homework(grade: cl5A , subject: "english" ,homework: "Written work", dueDate: "10-04-2015"  ,  message: "English Homework for whole 5 A Students ", gradeFlag: '1').save(flush: true)
				//  new Homework(homeworkId: 106 ,grade: cl5B , subject: "english" ,homework: "English homework", dueDate:  "9-04-2015" ,section: "B",  message: "English Homework for whole 5 B Students ", gradeFlag: '1').save(flush: true)
				//  new Homework(homeworkId: 107 ,grade: cl6A , subject: "history" ,homework: "history homework", dueDate:  "8-04-2015" ,section: "A" ,  message: "History Homework for whole 6 A Students ", gradeFlag: '1').save(flush: true)
				//  new Homework(homeworkId: 108 ,grade: cl7A , subject: "computerScience" ,homework: "computerScience homework", dueDate: "7-04-2015" ,section:"A",  message: "Computer Science Homework for whole 7 A Students ", gradeFlag: '1').save(flush: true)
				//  new Homework(homeworkId: 109 ,grade: cl10A , subject: "physics" ,homework: "Physics homework", dueDate:  "6-04-2015",section: "A",  message: "Physics Homework for whole 10 A Students ", gradeFlag: '1').save(flush: true)
				//  new Homework(homeworkId: 110 ,grade: cl10B , subject: "physics" ,homework: "Physics homework", dueDate:  "5-04-2015" ,section: "B",  message: "Chemistry Homework for whole 10 B Students ", gradeFlag: '1').save(flush: true)



			  //	End of homework entries


		   // Add exam entries Date startTime
   

					   new Exam(examId: 100 , examName: "English" , examType: "Class test").save(flush: true)
					  new Exam(examId: 101 , examName: "Chemistry" , examType: "Class test").save(flush: true)
					  new Exam(examId: 102 , examName: "Physics" , examType: "Model Exam").save(flush: true)
					  new Exam(examId: 103 , examName: "Mathematics" , examType: "Model Exam").save(flush: true)
					  new Exam(examId: 104 , examName: "Hindi" , examType: "ModelExam").save(flush: true)
					  new Exam(examId: 105 , examName: "History", examType: "Mid Term Exam").save(flush: true)
					  new Exam(examId: 106 , examName: "Computer Science", examType: "Mid Term Exam").save(flush: true)
					  

					  def exam1 , exam2 ,exam3 ,exam4 ,exam5,exam6,exam7
					  exam1 = Exam.get(1)
					  exam2 = Exam.get(2)
					  exam3 = Exam.get(3)
					  exam4 = Exam.get(4)
					  exam5 = Exam.get(5)
					  exam6 = Exam.get(6)
					  exam7 = Exam.get(7)
					 
					   
                     
					  new ExamSyllabus(exam: exam1 , subject: english ,syllabus: "English Syllabus" ).save(flush: true)
					  new ExamSyllabus(exam: exam1 , subject: chemistry,syllabus: "Chemistry Syllabus").save(flush: true)
					  new ExamSyllabus(exam: exam1 , subject: physics , syllabus: "Physics Syllabus").save(flush: true)
					  
					  def examSyllabus1 , examSyllabus2 ,examSyllabus3 
					  examSyllabus1 = ExamSyllabus.get(1)
					  examSyllabus2 = ExamSyllabus.get(2)
					  examSyllabus3 = ExamSyllabus.get(3)
					  
					  new ExamSchedule(exam: exam1  ,subjectSyllabus: examSyllabus1,  subject: english ,teacher :sibi,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam1  ,subjectSyllabus: examSyllabus2 , subject: chemistry ,teacher :mathew,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam1  ,subjectSyllabus: examSyllabus3, subject: physics ,teacher :sathees,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam2, ,subjectSyllabus: examSyllabus1, subject: maths ,teacher : sibi,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam2, ,subjectSyllabus: examSyllabus2 , subject: hindi ,teacher :mathew,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam2 ,subjectSyllabus: examSyllabus3, subject: history ,teacher :sathees,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					  new ExamSchedule(exam: exam3  ,subjectSyllabus: examSyllabus1 , subject: computerScience ,teacher :sibi,startTime: new Date(2014, 02, 11, 04, 30),endTime: new Date(2014, 02, 11, 04, 30)).save(flush: true)
					 
					  def examSchedule1, examSchedule2 ,examSchedule3,examSchedule4,examSchedule5,examSchedule6,examSchedule7
					   
					  examSchedule1 = ExamSchedule.get(1)
					  examSchedule2 = ExamSchedule.get(2)
					  examSchedule3 = ExamSchedule.get(3)
					  examSchedule4 = ExamSchedule.get(4)
					  examSchedule5 = ExamSchedule.get(5)
					  examSchedule6 = ExamSchedule.get(6)
					  examSchedule7 = ExamSchedule.get(7)
				
					  cl5A.addToExams(exam1).save(flush: true)
					  
					  
					  
 
					  exam1.addToExamSubjectSchedule(examSchedule1)
					  .addToExamSubjectSchedule(examSchedule2)
					 .addToExamSubjectSchedule(examSchedule3).save(flush: true)
  
					  
					
					
					 


				[cl5A ,cl5B,cl6A,cl6B,cl7A,cl7B].each { cls ->

					new TimeTable(grade: cls, day: "Monday", teacher: sibi, subject: english).save(flush: true)
					new TimeTable(grade: cls, day: "Monday", teacher: mathew, subject: maths).save(flush: true)
					new TimeTable(grade: cls, day: "Monday", teacher: sathees, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Monday", teacher: sibi, subject: history).save(flush: true)
					new TimeTable(grade: cls, day: "Monday", teacher: mathew, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Monday", teacher: sibi, subject: physics).save(flush: true)

					new TimeTable(grade: cls, day: "Tuesday", teacher: mathew, subject: physics).save(flush: true)
					new TimeTable(grade: cls, day: "Tuesday", teacher: sibi, subject: chemistry).save(flush: true)
					new TimeTable(grade: cls, day: "Tuesday", teacher: sathees, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Tuesday", teacher: sathees, subject: history).save(flush: true)
					new TimeTable(grade: cls, day: "Tuesday", teacher: mathew, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Tuesday", teacher: sibi, subject: chemistry).save(flush: true)

					new TimeTable(grade: cls, day: "Wednesday", teacher: sibi, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Wednesday", teacher: mathew, subject: maths).save(flush: true)
					new TimeTable(grade: cls, day: "Wednesday", teacher: sathees, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Wednesday", teacher: sibi, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Wednesday", teacher: mathew, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Wednesday", teacher: sibi, subject: physics).save(flush: true)

					new TimeTable(grade: cls, day: "Thursday", teacher: sibi, subject: history).save(flush: true)
					new TimeTable(grade: cls, day: "Thursday", teacher: mathew, subject: maths).save(flush: true)
					new TimeTable(grade: cls, day: "Thursday", teacher: sathees, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Thursday", teacher: sibi, subject: physics).save(flush: true)
					new TimeTable(grade: cls, day: "Thursday", teacher: mathew, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Thursday", teacher: sibi, subject: maths).save(flush: true)

					new TimeTable(grade: cls, day: "Friday", teacher: sibi, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Friday", teacher: mathew, subject: maths).save(flush: true)
					new TimeTable(grade: cls, day: "Friday", teacher: sathees, subject: hindi).save(flush: true)
					new TimeTable(grade: cls, day: "Friday", teacher: sibi, subject: history).save(flush: true)
					new TimeTable(grade: cls, day: "Friday", teacher: mathew, subject: computerScience).save(flush: true)
					new TimeTable(grade: cls, day: "Friday", teacher: sibi, subject: computerScience).save(flush: true)


				}




		User ravi , rani , manish , malini , mahadev
		ravi = User.findByUsername("ravi@test.com")
		rani = User.findByUsername("raani@test.com")
		malini = User.findByUsername("malini@test.com")
		mahadev = User.findByUsername("mahadev@test.com")


		Conversation testconv = new Conversation(fromId:ravi.username , toId: rani.username , title: "Test Conversation between ravi and raani" , inTrash: false,isRead: false,toDate: new Date(),fromDate: new Date())
		testconv.addToMessages(new Message(fromId: "ravi" , toId: "rani" , messageText: "Haai raani" , messageTime:new Date() ))
				.addToMessages(new Message(fromId: "raani" , toId: "ravi" , messageText: "Haai raviii" , messageTime: new Date()))
				.save()
		ravi.addToConversations(testconv)
		rani.addToConversations(testconv)
		ravi.save()
		rani.save()
		testconv = new Conversation(fromId:ravi.username , toId: rani.username , title: "Test Conversation between ravi and raani" , inTrash: false,isRead: false,toDate: new Date(),fromDate: new Date())
		testconv.addToMessages(new Message(fromId: "ravi" , toId: "rani" , messageText: "Again Haai raani" , messageTime:new Date() ))
				.addToMessages(new Message(fromId: "raani" , toId: "ravi" , messageText: "Again Haai raviii" , messageTime: new Date()))
				.save()
		ravi.addToConversations(testconv)
		rani.addToConversations(testconv)
		ravi.save()
		rani.save()


		testconv = new Conversation(fromId: mahadev.username , toId: malini.username , title: "Message from Mahadev to Malini" , inTrash: false , isRead: false , toDate: new Date() , fromDate: new Date() )
				.addToMessages(new Message(fromId: "Mahadev" , toId: "Malini" ,messageTime: new Date() , messageText: "Haai malini"))
				.addToMessages(new Message(fromId: "Malini" , toId: "Mahadev" , messageText: "Hello mahadev" , messageTime: new Date()))
				.save()
		malini.addToConversations(testconv).save()
		mahadev.addToConversations(testconv).save()

		











		JSON.createNamedConfig('thin') {
			it.registerObjectMarshaller( Grade ) { Grade grade ->

				def output = [:]
				output['grade'] = grade.name
				output['section'] = grade.section

				return output
			}
		}

		JSON.registerObjectMarshaller( Grade ) { Grade grade ->
			
							[
							gradeName : grade.name,
							section : grade.section,
							 student : grade.students.collect{ Student std ->
			[studentId: std.studentId, studentName: std.studentName
				]
							}]
						}

		JSON.createNamedConfig('homework') {
			it.registerObjectMarshaller( Homework ) { Homework home ->



				def output = [:]
				output['subject'] = home.subject
				output['dueDate'] = home.dueDate
				output['homeGivenDate'] = home.dateCreated
				output['details'] = home.message

				return output
			}
		}

		/*JSON.createNamedConfig('gradesubject') {
			it.registerObjectMarshaller( GradeSubject ) { GradeSubject gradeSubject ->

				def output = [:]
				output['gradeId'] = gradeSubject1.gradeId
				output['subjectId'] = gradeSubject1.subjectId

				return output
			}
		}*/
		JSON.createNamedConfig('student') {
			it.registerObjectMarshaller( Student ) { Student student ->

				def output = [:]
				output['studentId'] = student.studentId
				output['studentName'] = student.studentName

				return output
			}
		}

			/*JSON.registerObjectMarshaller( Guardian ) { Guardian fathert ->


					 return ['parentId': fathert.id,
				'parentName': fathert.name,
				'emailId':fathert.emailId, student:fathert.children]
			}*/


		JSON.registerObjectMarshaller(Student) {
			 Student student ->

				def output = [:]
				output['studentId'] = student.studentId
				output['studentName'] = student.studentName
				output['grade']=student.grade.name
				output['section']=student.grade.section
				output['classTeacherName']=student.grade.classTeacherName
				
				

				return output
			
		}
		JSON.registerObjectMarshaller(Subject) {
			Subject subject ->

			   def output = [:]
			   output['subjectId'] = subject.subjectId
			   output['subjectName'] = subject.subjectName
		
//			   return  ['subject':['subjectId	': subject.subjectId,
//				   'subjectName': subject.subjectName,
//				   'grade':subject.grade]]
			  
			   

			   return output
		   
	   }
		
		JSON.createNamedConfig('msg') {
			it.registerObjectMarshaller( Message ) { Message msg ->
			
					 return ['code': msg.code,
				'value': msg.value]
			}
		}
		
		
		JSON.registerObjectMarshaller(ExamSyllabus) {
			ExamSyllabus examSyllabus ->
		
			  
		   return  ['syllabus':  examSyllabus.syllabus,
				   'subjectName': examSyllabus.subject.subjectName,

			   
			   ]
			   
	   
		}
		
		JSON.registerObjectMarshaller(Exam) {
			Exam subject ->
		
			  
		   return  ['examId':  subject.examId?subject.examId.toString():'',
				   'examType': subject.examType,
			   'schedule':subject.examSubjectSchedule,
			   
			   
			   ]
			   
	   }
		
		JSON.registerObjectMarshaller(ExamSchedule) {
			ExamSchedule exSchedule ->
		
		   return  ['examSchedule':['subjectName':  exSchedule.subject.subjectName,
			    'subjectSyllabus':exSchedule.subjectSyllabus,
			   'teacherName':exSchedule.teacher.teacherName,
				   'examStartTime':exSchedule.startTime? exSchedule.startTime.format("yyyy-MM-dd hh:mm:ss a"):'date not',
			  
			   'examEndTime':exSchedule.endTime? exSchedule.startTime.format("yyyy-MM-dd hh:mm:ss a"):'date not' ]
			   
			   ]
			   
	   }
		
				JSON.createNamedConfig('exam') {
			it.registerObjectMarshaller( Grade ) { Grade msg ->
			
					 return ['exams': msg.exams
				]
			}
		}
			
		JSON.createNamedConfig('teacherC') {
			it.registerObjectMarshaller( Teacher ) { Teacher teach ->

			

				 return  ['teacher':['teacherId': teach.id,
				'teacherName': teach.teacherName,
				'emailId':teach.teacherEmailId,
				'grades' : teach.grades
				]]
			}
		}
		
		JSON.createNamedConfig('teacherSub') {
			it.registerObjectMarshaller( Teacher ) { Teacher teach ->

			

				 return  [
				'subjects' : teach.subject
				]
			}
		}
		JSON.createNamedConfig('Success') {
			it.registerObjectMarshaller( Success ) { Success success ->

				def output = [:]
				output['success'] = 0
				output['failure'] = 1


				return output
			}
		}
		// Marshellers for classes

		JSON.registerObjectMarshaller( Guardian ) { Guardian g ->
			return [

					name : g.name,
					educational_qualification : g.educational_qualification,
					profession : g.profession,
					username : g.designation,
					mobileNumber : g.mobileNumber,
					emailId : g.emailId,
					officeNumber : g.officeNumber,
					children : g.getChildren()


				   ]
		}

		JSON.createNamedConfig('ParentAccInfo') {
			it.registerObjectMarshaller( Guardian ) { Guardian g ->



				return  ['accountInfo':['username': g.username.toString(),
										'name': g.name,
										'educational_qualification' : g.educational_qualification ,
										'profession' : g.profession,
										'designation' : g.designation ,
										'mobileNumber' : g.mobileNumber ,
										'emailId' : g.emailId,
										'officeNumber' : g.officeNumber,
										'numberOfChildren' : g.getChildren()?.size().toString(),

											]

						]


			}
		}


		JSON.createNamedConfig('getChildren') {
			it.registerObjectMarshaller( Student ) { Student s ->



				return                  [
										  'studentId': s.studentId.toString(),
										  'registerNumber': s.registerNumber,
										  'studentName' : s.studentName ,
										  'grade' : s.grade?.name.toString(),
										  'section' : s.grade?.section,
										  'gender' : s.gender,
										  'present_address' : s.present_address ,
										  'no_of_siblings' : s.no_of_siblings.toString() ,
										  'dob' : s.dob,
										  'age' : s.getAge() ,
										  'present_guardian' : s.present_guardian
										  /* 'father' :   [
														   'id' : s.getFather()?.id,
														   'name' : s.getFather()?.name ,
														],
										  'mother' :   [
												  'id' : s.getMother()?.id,
												  'name' : s.getMother()?.name ,
										  ],
										  'local_guardian' :   [
												  'id' : s.getLocalGuardian()?.id,
												  'name' : s.getLocalGuardian()?.name ,
										  ]*/

										]
			}
		}


		JSON.createNamedConfig('studentDetail') {
			it.registerObjectMarshaller( Student ) { Student s ->



				return                  [
										  'studentId': s.studentId.toString(),
										  'registerNumber': s.registerNumber,
										  'studentName' : s.studentName ,
										  'grade' : s.grade?.name.toString(),
										  'section' : s.grade?.section,
										  'gender' : s.gender,
										  'present_address' : s.present_address ,
										  'no_of_siblings' : s.no_of_siblings.toString() ,
										  'dob' : s.dob,
										  'age' : s.getAge() ,
										  'present_guardian' : s.present_guardian,
										   'father' :   [
														   'id' : s.getFather()?.id.toString(),
														   'name' : s.getFather()?.name ,
														],
										  'mother' :   [
												  'id' : s.getMother()?.id,
												  'name' : s.getMother()?.name ,
										  ],
										  'local_guardian' :   [
												  'id' : s.getLocalGuardian()?.id,
												  'name' : s.getLocalGuardian()?.name ,
										  ]

									  ]
			}
		}





		JSON.registerObjectMarshaller( Student ) { Student s ->
			return [

					studentId : s.studentId.toString() ,
					registerNumber : s.registerNumber,
					studentName : s.studentName ,
					gender : s.gender ,
					present_address : s.present_address ,
					no_of_siblings : s.no_of_siblings.toString() ,
					dob : s.dob ,
					studentPhoto : s.studentPhoto ,
					present_guardian : s.present_guardian ,
					grade : s.grade?.name.toString() ,
					section : s.grade?.section ,
					//father: s?.getFather() ,
					//mother: s?.getMother() ,
					//local_guardian: s?.getLocalGuardian()

			]
		}

		JSON.registerObjectMarshaller( TimeTable ) { TimeTable t ->
			return [
					  subject : t.subject?.subjectName ,
					  day : t.day ,
					  teacher: t.teacher?.teacherName ,
					  grade: t.grade?.name ,
					  section: t.grade?.section



					]}

	JSON.registerObjectMarshaller( Address ) { Address a ->
			return [
					address : a.address ,
					place : a.place ,
					landmark: a.landmark

			]}


		JSON.registerObjectMarshaller( Homework ) { Homework h ->
			return [

					'homeworkList' : [

					homeworkId: h.homeworkId,
					grade : h.grade?.name,
					section : h.grade?.section ,
					subject: h.subject ,
					dueDate : h.dueDate ,
					homework: h.homework ,
					dateCreated : h.dateCreated ,
					student : h.student?.studentName ,
					studentId : h.student?.studentId ,
					message : h.message ,
					gradeFlag : h.gradeFlag


			]]
		}



		JSON.createNamedConfig('getTimeTable') {
			it.registerObjectMarshaller( TimeTable ) { TimeTable t ->



				return  [
							 subject: t.subject?.subjectName,
							 teacher: t.teacher?.teacherName ,
							 teacherId: t.teacher?.teacherId,
							 teacherPhoto: t.teacher?.teacherPhoto,
							 startTime : t.startTime ,
							 endTime : t.endTime
						 ]
			}
		}


		JSON.createNamedConfig('studentHomework') {
			it.registerObjectMarshaller( Homework ) { Homework h ->



				return  [
						 'homeworkId' : h.homeworkId?h.homeworkId.toString():'' ,
						 'subject'    : h.subject ,
						 'dueDate'    : h.dueDate?h.dueDate.format("yyyy-MM-dd").toString():''  ,
						 'dateCreated': h.dateCreated?h.dateCreated.format("yyyy-MM-dd").toString():'' ,
						 'message'    : h.message,
						 'homeworkType'   : h.homework
						]
			}
		}





		JSON.registerObjectMarshaller( Conversation  )
				{   cnv ->



					 return [
								 "threadId": cnv.threadId,
								 "numberOfMessages" : cnv.messages.size(),
								 "fromDate": cnv.fromDate.format('EEEE, dd MMMM yyyy, hh:mm:ss a'),
								 "fromId": cnv.fromId ,
								 "toId": cnv.toId,
								 "inTrash": cnv.inTrash,
								 "isRead": cnv.isRead,
								 "title": cnv.title,
								 "toDate": cnv.toDate.format('EEEE, dd MMMM yyyy, hh:mm:ss a'),
								 "messages" : cnv.messages



							]

				}
		JSON.createNamedConfig('msgList')
				{
					it.registerObjectMarshaller( Message ) { Message m ->
						return [
								  'messageId' : m.messageId,
								  'fromId' : m.fromId,
								  'toId' : m.toId ,
								  'messageText' : m.messageText,
								  'messageTime' : m.messageTime.format('EEEE, dd MMMM yyyy, hh:mm:ss a')

							   ]}
				}












		}

	def destroy = {
	}
}