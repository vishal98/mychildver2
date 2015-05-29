package ghumover2

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder



class ConversationController {
	static allowedMethods = [getUserConversations	: "GET" , getSentConversations:"GET"  , newMail :"POST" , replyMsg : "POST"]
	def springSecurityService

	User user

	static responseFormats = ['json', 'xml']


	def getUserConversations()
	{
		def output = [:]
		try {

			user  =    (params.userId)? ( (params.userId.isNumber()) ? (User.findById(Long.parseLong(params.userId))) : User.findByUsername(params.userId) )   : user;


			JSON.use('msgList'){
			    def inbox = Conversation.findAllByToId(user.username)

				output ['numberOfConversations'] = inbox.size()
				output ['conversations'] =  inbox
				render output as JSON
			}






		}
		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id "+ params.userId +" not found"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "Error occured "
			output['data'] = e
			render output as JSON
		}
	}


	


	def getSentConversations()
	{
		def output = [:]
		try {

			user = springSecurityService.isLoggedIn() ? springSecurityService.loadCurrentUser() : null

			JSON.use('msgList'){
				def sentbox = Conversation.findAllByFromId(user.username)

				output ['numberOfConversations'] = sentbox.size().toString()
				output ['conversations'] =  sentbox
				render output as JSON
			}






		}
		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id "+ params.userId +" not found"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "Error occured "
			output['data'] = e
			render output as JSON
		}
	}


















	def getCurUserConversations()
	{
		user = springSecurityService.isLoggedIn() ? springSecurityService.loadCurrentUser() : null
		if (user) {
			getUserConversations()

		}
		else
		{
			def output = [:]
			output['status'] = "error"
			output['message'] = "Not logged in"
			output['data'] = "NULL"
			render output as JSON

		}

	}


	def getConversationFromUser()
	{
		def output = [:]
		try{
			user = springSecurityService.isLoggedIn() ? springSecurityService.loadCurrentUser() : null
			User fromUser =  (params.userId)? ( (params.userId.isNumber()) ? (User.findById(Long.parseLong(params.userId))) : User.findByUsername(params.userId) )   : null;

			def conv = Conversation.findAllByFromIdAndToId(fromUser.username,user.username)
			JSON.use('msgList'){
				output ['numberOfConversations'] = conv.size()
				output ['conversations'] =  conv
				render output as JSON
			}
		}
		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id "+ params.userId +" not found"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "User Id "+ params.userId +" not found"
			output['data'] = "NULL"
			render output as JSON

		}
	}





	def getConversationFromAndTo()
	{
		def output = [:]
		try{
			User toUser =  (params.toId)? ( (params.toId.isNumber()) ? (User.findById(Long.parseLong(params.toId))) : User.findByUsername(params.toId) )   : null;
			User fromUser =  (params.fromId)? ( (params.fromId.isNumber()) ? (User.findById(Long.parseLong(params.fromId))) : User.findByUsername(params.fromId) )   : null;

			def conv = Conversation.findAllByFromIdAndToId(fromUser.username,toUser.username)
			JSON.use('msgList'){
				output ['numberOfConversations'] = conv.size()
				output ['conversations'] =  conv
				render output as JSON
			}
		}
		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id "+ params.userId +" not found"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "Some error occured"
			output['data'] = "NULL"
			render output as JSON

		}

	}






/*
	def saveMessage()
	{
		def output = [:]
		try{


			User toUser =  (params.toId)? ( (params.toId.isNumber()) ? (User.findById(Long.parseLong(params.toId))) : User.findByUsername(params.toId) )   : null;
			User fromUser =  (params.fromId)? ( (params.fromId.isNumber()) ? (User.findById(Long.parseLong(params.fromId))) : User.findByUsername(params.fromId) )   : null;
			Long threadId = Long.parseLong(params.threadId)
			String msg = params.messageText
			Conversation conversation = Conversation.findByThreadId(threadId)

			Message newMsg = new Message(messageText: msg , messageTime: new Date() , fromId: fromUser.username , toId: toUser.username)
			conversation.addToMessages(newMsg).save()
			output['status'] = "success"
			output['message'] = "message sent"
								   def savedMsg  = [:]
								   savedMsg['messageText'] = newMsg.messageText
								   savedMsg['messageTime'] = newMsg.messageTime
			output['data'] = savedMsg
			render output as JSON
			}

			

		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id  '"+ params.toId +"'  or  '"+params.fromId+"' not found. check Ids"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "Some error occured"
			output['data'] = "NULL"
			render output as JSON

		}

	}

*/




	def newMail()
	{
		def output = [:]
		try{


			User toUser =  (params.toId)? ( (params.toId.isNumber()) ? (User.findById(Long.parseLong(params.toId))) : User.findByUsername(params.toId) )   : null;
			User fromUser =  (params.fromId)? ( (params.fromId.isNumber()) ? (User.findById(Long.parseLong(params.fromId))) : User.findByUsername(params.fromId) )   : null;
			String msg = params.messageText
			String title = params.title
			Conversation conversation = new Conversation(fromId: fromUser.username , toId: toUser.username , title: title , inTrash: false,isRead: false ,toDate: new Date() )
										.addToMessages(new Message(messageText: msg , messageTime: new Date() , fromId: fromUser.username , toId: toUser.username))
										.save()


			fromUser.addToConversations(conversation).save()
			toUser.addToConversations(conversation).save()


			output['status'] = "success"
			output['message'] = "message sent"

			output['data'] = conversation
			test("new email from "+fromUser.username)
			render output as JSON
		}



		catch (NullPointerException ne)
		{
			output['status'] = "error"
			output['message'] = "User Id  '"+ params.toId +"'  or  '"+params.fromId+"' not found. check Ids"
			output['data'] = "NULL"
			render output as JSON
		}
		catch (Exception e)
		{
			output['status'] = "error"
			output['message'] = "Some error occured"
			output['data'] = "NULL"
			render e as JSON

		}

	}


	def test(String msg1){
		
		 
		 def rest = new RestBuilder()
		 def resp = rest.post("https://api.pushbots.com/push/all"){
			 header 'x-pushbots-appid', '550e9e371d0ab1de488b4569'
			 header 'x-pushbots-secret', 'e68461d7755b0d3733b4b36717aea77d'
	 json {
		   sound = "ding"
		  platform = ["0", "1"]
		 alias= ""
	 tags= ["MAIL"]
	 msg= msg1
	 except_active= []
	  except_tags= []
	   badge= "187192"
	 payload= {
		 type ="mail_box"	 }
	 active= []
	 }
	   
	 }
		 
		 
			System.out.print("resp val : "+resp.json)
		}
	












	def replyMsg()
	 {
		 def output = [:]
		 try {

			 Long threadId = Long.parseLong(params.threadId)

			 String messageText = params.messageText
			 String fromId = params.fromId
			 String toId = params.toId

			 if(new Message(threadId: threadId , messageText: messageText , fromId: fromId , toId: toId , messageTime: new Date() ).save())
			 {
				 output['status'] = "success"
				 output['message'] = "Message sent"
				 output['data'] = messageText
				 render output as JSON
			 }
			 else
			 {
				 output['status'] = "failed"
				 output['message'] = "Message not sent"
				 output['data'] = messageText
				 render output as JSON
			 }

		 }
		 catch (Exception e)
		 {
			 render e
		 }

	}

	 def getCurUserConversationsV1()
	 {
		 def output = [:]
		 user = springSecurityService.isLoggedIn() ? springSecurityService.loadCurrentUser() : null
		 if (user) {
			 user  =    (params.userId)? ( (params.userId.isNumber()) ? (User.findById(Long.parseLong(params.userId))) : User.findByUsername(params.userId) )   : user;
 
 
			 JSON.use('conversationVer1'){
				 def inbox = Conversation.findAllByToId(user.username)
 
				 output ['numberOfConversations'] = inbox.size()
				 output ['conversations'] =  inbox
				 render output as JSON
			 }
 
 
		 }
		 else
		 {
 
			 output['status'] = "error"
			 output['message'] = "Not logged in"
			 output['data'] = "NULL"
			 render output as JSON
 
		 }
 
	 }
 





}
