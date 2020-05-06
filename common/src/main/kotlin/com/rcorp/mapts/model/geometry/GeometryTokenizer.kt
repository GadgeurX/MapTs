package com.rcorp.mapts.model.geometry

import kotlin.collections.ArrayList

object GeometryTokenizer {
  fun tokenize(string:String, delimiter:Char):List<String> {
    val tokens = ArrayList<String>()
    val stack = ArrayList<Char>()
    var consumed = 0
    for (position in 0 until string.length)
    {
      val character = string.get(position)
      if ((character == '(') || (character == '['))
      {
        stack.add(0, character)
      }
      else if ((((character == ')') && (stack[0] == '(')) || ((character == ']') && (stack[0] == '['))))
      {
        stack.removeAt(0)
      }
      if ((character == delimiter) && (stack.size == 0))
      {
        tokens.add(string.substring(consumed, position))
        consumed = position + 1
      }
    }
    if (consumed < string.length)
    {
      tokens.add(string.substring(consumed))
    }
    return tokens
  }
  fun removeLeadingAndTrailingStrings(string:String, leadingString:String, trailingString:String):String {
    var startIndex = string.indexOf(leadingString)
    if (startIndex == -1)
    {
      startIndex = 0
    }
    else
    {
      startIndex += leadingString.length
    }
    var endIndex = string.lastIndexOf(trailingString)
    if (endIndex == -1)
    {
      endIndex = string.length
    }
    return string.substring(startIndex, endIndex)
  }
}