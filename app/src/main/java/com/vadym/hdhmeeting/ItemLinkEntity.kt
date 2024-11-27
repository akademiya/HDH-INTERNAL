package com.vadym.hdhmeeting

import java.io.Serializable

class ItemLinkEntity : Serializable {
    var linkID: Int = 0
    var linkTitle: String? = null
    var linkUrl: String? = null
    var days: List<String>? = null
    var time: String? = null
    var position = 0

    constructor(linkTitle: String?, linkUrl: String?, days: List<String>?, time: String?) {
        this.linkTitle = linkTitle
        this.linkUrl = linkUrl
        this.days = days
        this.time = time
    }

    constructor(linkTitle: String?, linkUrl: String?, days: List<String>?, time: String?, position: Int) {
        this.linkTitle = linkTitle
        this.linkUrl = linkUrl
        this.days = days
        this.time = time
        this.position = position
    }

    constructor(linkID: Int, linkTitle: String?, linkUrl: String?, days: List<String>?, time: String?, position: Int) {
        this.linkID = linkID
        this.linkTitle = linkTitle
        this.linkUrl = linkUrl
        this.days = days
        this.time = time
        this.position = position
    }
}

