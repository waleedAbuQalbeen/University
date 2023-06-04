package org.example.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.log4j.Logger;
import org.example.dao.TeachersDao;
import org.example.model.Teacher;
import org.example.util.MessageGenerator;
import org.example.util.Role;
import org.example.util.Writer;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;
import static org.example.util.Role.COURSE;
import static org.example.util.Role.TEACHER;
import static org.example.util.Writer.*;

@WebServlet(urlPatterns = "/teacher")
public class TeacherController extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(TeacherController.class.getName());
    private TeachersDao teachersDao;
    private Gson gson;

    public void init() {
        teachersDao = new TeachersDao();
        gson = new GsonBuilder().create();
    }

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) throws IOException {
        try {
            List<Teacher> teacherList = teachersDao.getAll();
            String teachersJson = gson.toJson(teacherList);
            LOGGER.info("Get all teachers: " + teachersJson);
            Writer.write(response, SC_OK, JSON_CONTENT, teachersJson);

        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }

    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        Teacher teacher = gson.fromJson(reader, Teacher.class);

        try {
            teachersDao.add(teacher);
            LOGGER.info(MessageGenerator.insertedSuccessfully(TEACHER, teacher.toString()));
            Writer.write(response, SC_OK, TEXT_CONTENT,
                    MessageGenerator.insertedSuccessfully(TEACHER, teacher.toString()));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        BufferedReader reader = request.getReader();
        Teacher teacher = gson.fromJson(reader, Teacher.class);
        try {
            teachersDao.update(teacher);
            LOGGER.info(MessageGenerator.updatedSuccessfully(TEACHER, teacher.toString()));
            Writer.write(response, SC_OK, TEXT_CONTENT,
                    MessageGenerator.updatedSuccessfully(TEACHER, teacher.toString()));
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParameter = request.getParameter("id");

        if (idParameter == null) {
            LOGGER.error(MessageGenerator.missingIdParameter(TEACHER));
            Writer.write(response, SC_BAD_REQUEST,
                    TEXT_CONTENT,
                    MessageGenerator.missingIdParameter(TEACHER));
        } else {
            int teacherId = Integer.parseInt(idParameter);
            try {
                teachersDao.delete(teacherId);
                LOGGER.info(MessageGenerator.deletedSuccessfully(TEACHER, teacherId));
                Writer.write(response, SC_OK, TEXT_CONTENT,
                        MessageGenerator.deletedSuccessfully(TEACHER, teacherId));

            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }


}
