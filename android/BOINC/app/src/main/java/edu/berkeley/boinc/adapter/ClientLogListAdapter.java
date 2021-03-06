/*
 * This file is part of BOINC.
 * http://boinc.berkeley.edu
 * Copyright (C) 2016 University of California
 *
 * BOINC is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * BOINC is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with BOINC.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.berkeley.boinc.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import edu.berkeley.boinc.R;
import edu.berkeley.boinc.rpc.Message;

public class ClientLogListAdapter extends ArrayAdapter<Message> {
    private List<Message> entries;
    private Activity activity;

    public static class ViewEventLog {
        int entryIndex;
        TextView tvMessage;
        TextView tvDate;
        TextView tvProjectName;
    }

    public ClientLogListAdapter(Activity activity, ListView listView, int textViewResourceId, List<Message> entries) {
        super(activity, textViewResourceId, entries);
        this.entries = entries;
        this.activity = activity;

        listView.setAdapter(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    public String getDateTimeString(int position) {
        final Instant instant = Instant.ofEpochSecond(entries.get(position).getTimestamp());
        return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)
                                .format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
    }

    @Override
    public Message getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getMessage(int position) {
        return entries.get(position).getBody();
    }

    public String getProject(int position) {
        return entries.get(position).getProject();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View vi = convertView;
        ViewEventLog viewEventLog;

        // Only inflate a new view if the ListView does not already have a view assigned.
        if(convertView == null) {
            vi = ContextCompat.getSystemService(activity, LayoutInflater.class)
                    .inflate(R.layout.eventlog_client_listitem_layout, null);

            viewEventLog = new ViewEventLog();
            viewEventLog.tvMessage = vi.findViewById(R.id.msgs_message);
            viewEventLog.tvDate = vi.findViewById(R.id.msgs_date);
            viewEventLog.tvProjectName = vi.findViewById(R.id.msgs_project);

            vi.setTag(viewEventLog);
        }
        else {
            viewEventLog = (ViewEventLog) vi.getTag();
        }

        // Populate UI Elements
        viewEventLog.entryIndex = position;
        viewEventLog.tvMessage.setText(getMessage(position));
        viewEventLog.tvDate.setText(getDateTimeString(position));
        if(getProject(position).isEmpty()) {
            viewEventLog.tvProjectName.setVisibility(View.GONE);
        }
        else {
            viewEventLog.tvProjectName.setVisibility(View.VISIBLE);
            viewEventLog.tvProjectName.setText(getProject(position));
        }

        return vi;
    }
}
