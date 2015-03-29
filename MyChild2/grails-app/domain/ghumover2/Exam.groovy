package ghumover2

class Exam {

	Long examId
	String examName
	String examType

	Grade grade


	static hasMany = [examSubjectSchedule:ExamSchedule ]


	static mapping = {
		id generator: 'increment',name: 'examId'
	}


	static constraints = {

		grade(nullable:true)
	

	}
	
	
	
	
	
}
