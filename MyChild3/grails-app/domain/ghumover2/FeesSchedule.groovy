package ghumover2

import grails.rest.Resource

@Resource(formats=['json', 'xml'])
class FeesSchedule {


    FeesTypeInterval interval
    Integer seqNo
    Float percentage

    static constraints = {
        percentage (scale: 2)
        seqNo(nullable: true)
        percentage(nullable: true)
      }
}
