package org.codehaus.groovy.grails.web.mapping

import org.codehaus.groovy.grails.web.taglib.AbstractGrailsTagTests

/**
 * @author Graeme Rocher
 * @since 1.0
 */
class ReverseUrlMappingTests extends AbstractGrailsTagTests {

    protected void onSetUp() {
        gcl.parseClass '''
public class CustomUrlMappings {
static mappings = {
"/$mslug/$controller/$action/$id?" {}

"/controller_name/$mslug/action_name/$nslug" {
    controller = "controller_name"
    action = "action_name"
}

"/controller_name/$mslug/action_name/$nslug/$oslug" {
    controller = "controller_name"
    action = "action_name"
}

name myNamedMapping: '/people/list' {
  controller = 'person'
  action = 'list'
}

name myOtherNamedMapping: "/showPeople/$lastName" {
  controller = 'person'
  action = 'byLastName'
}

"/attributed-model/instances/byType/$id" {
    controller = 'person'
    action = [GET: 'retrieve']
}
}}'''

        gcl.parseClass '''
class ProductController {
    def create = {}
    def save = {}
    def retrieve = {}
}
'''
    }

    void testLinkTagRendering() {
        def template1 = '<g:link controller="product" action="create" params="[mslug:mslug]">New Product</g:link>'
        assertOutputEquals '<a href="/acme/product/create">New Product</a>', template1, [mslug:"acme"]

        def template2 = '<g:link controller="controller_name" action="action_name" params="[mslug:mslug,nslug:nslug]">New Product</g:link>'
        assertOutputEquals '<a href="/controller_name/acme/action_name/Coyote">New Product</a>', template2, [mslug:"acme",nslug:"Coyote"]

        def template3 = '<g:link controller="controller_name" action="action_name" params="[mslug:mslug,nslug:nslug,extra:extra]">New Product</g:link>'
        assertOutputEquals '<a href="/controller_name/acme/action_name/Coyote?extra=RoadRunner">New Product</a>', template3, [mslug:"acme",nslug:"Coyote",extra:"RoadRunner"]

        def template4 = '<g:link controller="controller_name" action="action_name" params="[mslug:mslug,extra:extra,nslug:nslug]">New Product</g:link>'
        assertOutputEquals '<a href="/controller_name/acme/action_name/Coyote?extra=RoadRunner">New Product</a>', template4, [mslug:"acme",nslug:"Coyote",extra:"RoadRunner"]

        def template5 = '<g:link controller="controller_name" action="action_name" params="[mslug:mslug,nslug:nslug,oslug:oslug]">New Product</g:link>'
        assertOutputEquals '<a href="/controller_name/acme/action_name/Coyote/RoadRunner">New Product</a>', template5, [mslug:"acme",nslug:"Coyote",oslug:"RoadRunner"]

        def template6 = '<g:link mapping="myNamedMapping">List People</g:link>'
        assertOutputEquals '<a href="/people/list">List People</a>', template6, [:]

        def template7 = '<g:link mapping="myOtherNamedMapping" params="[lastName:\'Keenan\']">List People</g:link>'
        assertOutputEquals '<a href="/showPeople/Keenan">List People</a>', template7, [:]
        
        def template8 = '<g:link action="retrieve" controller="person" id="42">Link</g:link>'
        assertOutputEquals '<a href="/attributed-model/instances/byType/42">Link</a>', template8, [:]
    }
}
