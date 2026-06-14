package com.neilb.nonocraft.data.data_source.converter

import com.google.gson.reflect.TypeToken
import com.neilb.nonocraft.domain.model.Block

class PuzzleConverter :
    BaseConverter<Block>(object : TypeToken<List<List<Block>>>() {}.type)